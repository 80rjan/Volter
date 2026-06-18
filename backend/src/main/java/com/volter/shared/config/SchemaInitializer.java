package com.volter.shared.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns all DDL via Flyway (Hibernate ddl-auto is disabled).
 * <p>
 * Two independent migration sets are run:
 * - db/migration/public  -> applied once to the shared `public` schema
 * - db/migration/tenant  -> applied to each shop schema
 * <p>
 * The tenant migrations declare cross-schema foreign keys against
 * `public.staff`, so the public schema MUST be migrated first.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaInitializer {

    private static final String PUBLIC_SCHEMA = "public";
    private static final String PUBLIC_LOCATION = "classpath:db/migration/public";
    private static final String TENANT_LOCATION = "classpath:db/migration/tenant";

    private final DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("Initializing database schemas via Flyway...");

        // Step 1: migrate the shared public schema (must run before any tenant).
        migratePublic();

        // Step 2: migrate every shop schema that already exists.
        getShopSchemas().forEach(this::initializeTenantSchema);

        log.info("Schema initialization complete.");
    }

    /**
     * Provision (or upgrade) a single tenant schema. Call when a new shop registers.
     */
    public void initializeTenantSchema(String schemaName) {
        log.info("Migrating tenant schema: {}", schemaName);
        Flyway.configure()
                .dataSource(dataSource)         // uses app's connection pool
                .schemas(schemaName)            // sets the schema that Flyway manages, which creates the schema if absent
                .defaultSchema(schemaName)      // sets the schema that is used for migrations during this Flyway run
                .locations(TENANT_LOCATION)     // location of tenant migration scripts
                .table("flyway_schema_history") // table for keeping track of applied migrations (one per schema)
                .load()                         // creates a Flyway instance with the above configuration
                .migrate();                     // applies any pending migrations to the tenant schema
    }

    private void migratePublic() {
        log.info("Migrating public schema");
        // No baselineOnMigrate: greenfield expects an empty public schema. A
        // pre-existing, unmanaged schema should fail loudly rather than be
        // silently baselined past V1.
        Flyway.configure()
                .dataSource(dataSource)
                .schemas(PUBLIC_SCHEMA)
                .defaultSchema(PUBLIC_SCHEMA)
                .locations(PUBLIC_LOCATION)
                .table("flyway_schema_history")
                .load()
                .migrate();
    }

    private List<String> getShopSchemas() {
        List<String> schemas = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            boolean shopExists = conn.getMetaData()
                    .getTables(null, PUBLIC_SCHEMA, "shop", null).next();
            if (!shopExists) return schemas;

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT schema_name FROM public.shop")) {
                while (rs.next()) schemas.add(rs.getString("schema_name"));
            }
        } catch (SQLException e) {
            log.warn("Could not query existing shop schemas: {}", e.getMessage());
        }
        return schemas;
    }
}
