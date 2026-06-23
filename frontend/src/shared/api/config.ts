// Backend API base. In production the real URL is baked in at build time via the
// VITE_API_BASE build arg (see docker-compose.prod.yml); locally it falls back to
// the dev backend on localhost:8080.
export const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";

// EUR → MKD conversion rate (the denar is pegged to the euro at ~61.5).
export const EUR_TO_MKD = 61.5;
