# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./mvnw spring-boot:run          # Run the app
./mvnw test                     # Run all tests
./mvnw test -Dtest=PawnServiceTest  # Run a single test class
./mvnw compile                  # Compile only
./mvnw clean install            # Full build + tests
```

The app requires a `.env` file at the project root with PostgreSQL and JWT config (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_CONFIG_SECRET`). Tests use H2 in-memory DB.

## Architecture

**Stack**: Spring Boot 4.0.3, Java 21, PostgreSQL, Hibernate 6, MapStruct, Lombok, JWT (JJWT 0.12.5).

**Top-level package layout** (`com.volter`):
- `identity/` — Auth context: users, shops, roles, JWT security, registration/login
- `shared/` — Cross-cutting: Hibernate config, multi-tenancy infrastructure
- `shop/modules/` — Business domain split into 10 modules: `pawn`, `sale`, `inventory`, `customer`, `staff`, `cashregister`, `alert`, `expense`, `reporting`, `transaction`

**Module structure** (every business module follows this pattern):
```
{module}/
├── api/           # REST controller
├── application/   # Service (use cases) + DTOs (request/response/filter/result)
├── domain/
│   ├── model/     # JPA entities, enums, value objects, domain events
│   ├── repository/ # Spring Data JPA interfaces
│   └── specification/ # Spring Data Specifications for dynamic filtering
└── infrastructure/ # MapStruct mapper
```

## Multi-tenancy

Schema-per-tenant isolation. Each shop gets its own PostgreSQL schema; the `identity` context lives in the `public` schema.

- `TenantFilter` (security chain) extracts the shop ID from the JWT claim and sets it on `TenantContext` (ThreadLocal).
- `TenantIdentifierResolver` + `SchemaConnectionProvider` route Hibernate to the correct schema per request.
- `SchemaInitializer` runs at `ApplicationReadyEvent`: creates tables in the public schema and in every existing shop schema. When a new shop registers, call `SchemaInitializer.initializeTenantSchema(schemaName)` to provision its schema immediately.

DDL mode is `update` (Hibernate auto-updates schema on startup). Flyway is a dependency but currently unused.

## Key patterns

**Value objects**: `Money` and `PawnPeriod` are `@Embeddable` with `@AttributeOverride` in owning entities. Do not map them as separate entities.

**Domain events**: The `pawn` module emits events (`PawnCreatedEvent`, `PawnRenewedEvent`, etc.) that are persisted to a `pawn_event` table — not published to a message broker. Events record state changes for audit.

**Factories**: `CustomerFactory` and `ItemFactory` implement create-or-fetch semantics (idempotent). Use these instead of constructing entities directly in services.

**Risk alerts**: Certain business operations auto-create `RiskAlert` records. This is done inside service methods (e.g., pawn modification or underpayment triggers an alert). The alert module has no inbound dependencies — other modules call it directly.

**Specifications**: Dynamic filtering (pagination + criteria) is handled with `Specification<T>` in each module's `domain/specification/` package. Services accept filter DTOs and build specs; never build JPQL/native queries in controllers.

**Roles**: `ADMIN`, `MANAGER`, `EMPLOYEE`, `INTERN`. Role-based guards are applied both in `SecurityConfig` (URL-level) and via `@PreAuthorize` on service methods. Some business rules differ by role (e.g., underpayment alerts are not created for ADMIN/MANAGER actions).

## Testing

Tests use JUnit 5 + Mockito. Repositories and mappers are mocked; services are unit-tested in isolation. The security context and tenant context are not set up in unit tests — test only the service logic.

Run a specific test method: `./mvnw test -Dtest=PawnServiceTest#testCreatePawn`
