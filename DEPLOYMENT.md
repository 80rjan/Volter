# Deployment

How Volter is run in **development** and **production**, and how to deploy it.

One codebase produces two environments:

| | **dev** | **prod** |
|---|---|---|
| Where | your laptop | DigitalOcean droplet |
| Database | throwaway, demo data seeded | real, no demo data |
| Frontend | Vite dev server (hot reload) | static build served by nginx |
| API URL | `http://localhost:8080` | `/api` (same origin) |
| Public access | localhost | Cloudflare Tunnel + Access |
| Spring profile | `dev` | `prod` |

---

## Concepts

### The `/api` prefix (same-origin reverse proxy)

In production the UI and the API live at the **same address** (`https://erp.<domain>`) and are told apart by the path:

```
https://erp.<domain>/            → the UI (HTML/JS/CSS)
https://erp.<domain>/api/pawns   → the backend
```

`nginx` (the `frontend` container) inspects each request:

- path starts with `/api/` → forward it to the `backend` container (`backend:8080`) and relay the response;
- anything else → serve the static UI files.

```
browser ─► https://erp.<domain>/api/pawns
                 │
               nginx ── "/api/" ─► backend:8080/pawns ─► postgres   (private network)
               nginx ◄── response ◄────────────────────┘
browser ◄────────┘
```

`/api` is just a URL prefix that means "this request is for the backend." The browser never talks to the backend directly — only to nginx. This means **no CORS**, one TLS cert, and the backend is never exposed to the internet. `VITE_API_BASE=/api` tells the frontend to send data calls to `/api/...`.

### Cloudflare Tunnel

Normally a server must open an inbound port to be reachable, and the whole internet can knock on it. A **tunnel** removes that door: the `cloudflared` program dials **outbound** to Cloudflare and keeps the connection open. Visitor traffic arrives at Cloudflare (DNS points there, not at the droplet) and is pushed back down that open line to `cloudflared`, which hands it to nginx.

Result: the droplet opens **zero inbound ports**, its IP is hidden, and all traffic enters through Cloudflare — which lets us gate it with **Cloudflare Access**.

### Cloudflare Access (Zero Trust)

An identity gate that runs **inside Cloudflare, in front of the app**. Before any request reaches the server, the visitor must authenticate (email one-time code) and match a policy (allowed email, and optionally allowed IP). "Zero trust" = every request is checked against *who you are*, not whether you're on some network.

### Defense in depth

Three independent gates, plus a firewall:

1. **Cloudflare Access** — who can reach the page at all.
2. **App login (JWT)** — who is a valid user.
3. **Roles/permissions** — what that user may do.
4. **DigitalOcean firewall** — only SSH inbound; the app is reachable solely through the tunnel.

---

## Database: migrations vs seeds

DDL is owned by **Flyway** (Hibernate never generates schema). `SchemaInitializer` runs Flyway on startup: the shared `public` schema first, then every shop schema (schema-per-tenant).

```
db/migration/public/   ALWAYS   V1 schema · V2 permissions · V3 roles+grants
db/seed/public/        DEV ONLY  V4 demo shop + staff hierarchy
db/migration/tenant/   ALWAYS   V1 per-shop schema
db/seed/tenant/        DEV ONLY  V2 ~3 months of demo business data
```

Which locations are used is **configured per profile**, not decided in code:

- `application.properties` (default): schema folders only.
- `application-dev.properties`: schema **+** seed folders → demo data loaded.
- `application-prod.properties`: schema folders only → **no demo data**.

So production applies schema + reference data (roles/permissions) and never the demo records.

---

## Environment files

`.env.dev` and `.env.prod` hold the values and are **gitignored** (never committed). The Spring profile is **not** in these files — it's hardcoded in each compose file (`SPRING_PROFILES_ACTIVE`), so prod can't be flipped into dev mode by a bad env file.

### `.env.dev`

```ini
POSTGRES_USER=volter_user
POSTGRES_PASSWORD=devpassword
POSTGRES_DB=volter_dev
JWT_CONFIG_SECRET=<any base64 32-byte string>
ALLOWED_ORIGINS=http://localhost:5173
GOLD_API_KEY=                     # optional
```

### `.env.prod`

```ini
POSTGRES_USER=volter_prod
POSTGRES_PASSWORD=<strong password>
POSTGRES_DB=volter
JWT_CONFIG_SECRET=<openssl rand -base64 32>
ALLOWED_ORIGINS=https://erp.<your-domain>
GOLD_API_KEY=<goldapi.io key>
VITE_API_BASE=/api                # same-origin; keep as /api
TUNNEL_TOKEN=<from Cloudflare>    # see step 3 below
```

| Variable | Purpose |
|---|---|
| `POSTGRES_USER/PASSWORD/DB` | database credentials (also build the backend's datasource URL) |
| `JWT_CONFIG_SECRET` | signing secret for auth tokens |
| `ALLOWED_ORIGINS` | CORS origin (belt-and-suspenders; app is same-origin in prod) |
| `GOLD_API_KEY` | goldapi.io key for the live gold price (optional) |
| `VITE_API_BASE` | API base baked into the frontend **at build time** |
| `TUNNEL_TOKEN` | credential for the Cloudflare tunnel |

---

## Running locally (dev)

```bash
docker compose --env-file .env.dev -f docker-compose.dev.yml up --build
```

- UI: http://localhost:5173 (hot reload)
- API: http://localhost:8080
- DB: localhost:5432 (exposed for a GUI client)
- Demo data is seeded; log in as `user` / `password`.

Run just the dev database (e.g. when running the backend on the host via `./mvnw spring-boot:run`):

```bash
docker compose --env-file .env.dev -f docker-compose.dev.yml up postgres
```

---

## CI/CD (GitHub Actions → GHCR)

`.github/workflows/ci-cd.yml` builds in Actions (never on the droplet) and publishes to GHCR:
`ghcr.io/80rjan/volter-backend` and `ghcr.io/80rjan/volter-frontend`. Image build auth uses the
built-in `GITHUB_TOKEN` — no secrets needed for the build itself.

| Trigger | Gate (tests + lint/build) | Images published | Auto-deploy |
|---|---|---|---|
| PR to `main` | ✅ | — | — |
| Push to `main` | ✅ | `edge`, `sha-<commit>` | — |
| Push tag `vX.Y.Z` | ✅ | `X.Y.Z`, `X.Y`, `X`, `latest`, `sha-<commit>` | ✅ to droplet |

So `main` is integration (the `edge` tag is the latest main build, handy for a staging check), and
**production is driven by version tags**. `latest` always points at the newest release.

### Releasing (semantic versioning)

You choose the bump based on the change:

```bash
git tag v1.4.3 && git push origin v1.4.3   # bug fix      → patch
git tag v1.5.0 && git push origin v1.5.0   # new feature  → minor
git tag v2.0.0 && git push origin v2.0.0   # breaking     → major
```

Pushing the tag runs the gate, builds + publishes the versioned images, then auto-deploys that exact
version to the droplet. To deploy/roll back **manually** instead, pin `IMAGE_TAG` (see *Common operations*).

### Auto-deploy setup (one-time)

The deploy job reaches the droplet over **Tailscale** (no inbound ports opened — same posture as the
Cloudflare tunnel; chosen because the containerized `cloudflared` can't reach the host's SSH without
host networking). Set this up once:

1. **Tailscale on the droplet:** install Tailscale and `tailscale up`. In the Tailscale admin console
   create an **OAuth client** (scopes: `auth_keys`, tagged `tag:ci`) and an ACL that lets `tag:ci`
   SSH to the droplet.
2. **Deploy SSH key:** generate a keypair; add the **public** key to the droplet user's
   `~/.ssh/authorized_keys`; keep the **private** key for the secret below.
3. **GitHub repo secrets** (Settings → Secrets and variables → Actions):

   | Secret | Value |
   |---|---|
   | `TS_OAUTH_CLIENT_ID` / `TS_OAUTH_SECRET` | Tailscale OAuth client credentials |
   | `DROPLET_HOST` | droplet's Tailscale IP or MagicDNS name |
   | `DROPLET_USER` | SSH user on the droplet (e.g. the deploy user) |
   | `DROPLET_SSH_KEY` | the **private** deploy key (full contents) |
   | `DROPLET_PROJECT_DIR` | path to the repo on the droplet (holds the compose file + `.env.prod`) |

   The droplet must also be logged in to GHCR (step 4) so the pull succeeds.

Until these secrets exist the `deploy` job simply fails (the build still publishes); set them before
pushing your first `v*` tag.

---

## Deploying to production (DigitalOcean)

### 1. Droplet

- Create an Ubuntu droplet, install Docker + the Compose plugin.
- Clone the repo, create `.env.prod` (see above) with **real** secrets.
- Generate the JWT secret: `openssl rand -base64 32`.

### 2. A domain on Cloudflare

- Add your domain to Cloudflare (free plan); point the registrar's nameservers at Cloudflare.
- Pick a hostname, e.g. `erp.<your-domain>`.

### 3. Create the tunnel

- Cloudflare dashboard → **Zero Trust** → **Networks → Tunnels → Create** (type "Cloudflared") → name it.
- Copy the **token** it shows; put it in `.env.prod` as `TUNNEL_TOKEN`. (Do **not** run Cloudflare's install command — our `cloudflared` container runs the tunnel.)
- In the tunnel's **Public Hostnames** → Add:
  - Subdomain `erp`, your domain
  - Service: **HTTP**, URL **`frontend:80`** (cloudflared reaches the frontend container by name on the compose network)

### 4. Authenticate the droplet to GHCR

Images are built by CI and published to the GitHub Container Registry (GHCR) as
`ghcr.io/80rjan/volter-backend` and `ghcr.io/80rjan/volter-frontend`. If the
packages are private, the droplet must log in once so it can pull them:

```bash
# PAT needs only the read:packages scope. Create at:
# GitHub → Settings → Developer settings → Personal access tokens (classic)
echo "<PAT>" | docker login ghcr.io -u 80rjan --password-stdin
```

The login is persisted in `~/.docker/config.json`, so this is a one-time step
(skip it entirely if you make the GHCR packages public).

### 5. Start the stack

```bash
docker compose --env-file .env.prod -f docker-compose.prod.yml pull
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d
```

`erp.<your-domain>` should now load through the tunnel.

### 6. Lock it down — Cloudflare Access

- Zero Trust → **Access → Applications → Add → Self-hosted**; domain `erp.<your-domain>`.
- Login method: **One-time PIN** (email code; no Google needed).
- Add policies (first matching **Allow** wins; everything else denied):

  | Policy | Action | Include | Require |
  |---|---|---|---|
  | Super admin | Allow | Emails: admin email | — |
  | Shop 1 | Allow | Emails: shop-1 staff | IP ranges: shop 1 public IP `/32` |
  | Shop 2 | Allow | Emails: shop-2 staff | IP ranges: shop 2 public IP `/32` |

  `Include` = who; `Require` = an extra AND-condition. Shop staff get in only with an allowlisted email **and** from the shop IP; the super admin gets in with just their email, from anywhere.

### 7. DigitalOcean firewall

- Create a Cloud Firewall: **inbound = SSH (22) only**; outbound = all (cloudflared needs it).
- Nothing else is published, so the droplet is invisible to the public internet.

> **Static IPs:** the Shop 1/2 IP rules require each shop's public IP to be static (ask the ISP). If a shop IP is dynamic, it will lock staff out when it changes — fall back to email-only for that shop or update the rule.

---

## Common operations

```bash
# Logs
docker compose --env-file .env.prod -f docker-compose.prod.yml logs -f backend

# Restart one service
docker compose --env-file .env.prod -f docker-compose.prod.yml restart backend

# Manual deploy of the newest RELEASE (latest = newest vX.Y.Z tag)
docker compose --env-file .env.prod -f docker-compose.prod.yml pull
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d

# Roll back / pin to a specific version (any published tag: 1.4.2, 1.4, sha-1a2b3c4)
IMAGE_TAG=1.4.2 docker compose --env-file .env.prod -f docker-compose.prod.yml pull
IMAGE_TAG=1.4.2 docker compose --env-file .env.prod -f docker-compose.prod.yml up -d

# On-droplet debug of the frontend (not public)
curl http://localhost:8081

# Database backup
docker exec volter_db_prod pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB" > backup.sql
```

---

## Troubleshooting

- **Tunnel not connecting** — check `docker compose ... logs cloudflared`; verify `TUNNEL_TOKEN` and that the Public Hostname points to `frontend:80`.
- **UI loads but data calls fail** — confirm `VITE_API_BASE=/api` was set at **build** time (rebuild the frontend image after changing it), and that nginx `/api` proxies to `backend:8080`.
- **Locked out by an IP rule** — a shop's public IP changed; update the Access policy or temporarily relax it to email-only.
- **Flyway checksum/validation error** — the database has migrations applied that differ from the files (e.g. switching a populated dev DB to prod settings). Use a fresh database/volume per environment.
