package com.volter.shared.config;

import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaInitializer {

    private final DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("Initializing database schemas...");

        // Step 1: create identity tables in public schema
        updateTables("public", "com.volter.identity.domain.model");

        // Step 2: for every shop already registered, create its tenant tables
        getShopSchemas().forEach(schema -> {
            createSchema(schema);
            updateTables(schema, "com.volter.shop");
        });

        log.info("Schema initialization complete.");
    }

    // Call this whenever a new shop is created
    public void initializeTenantSchema(String schemaName) {
        log.info("Initializing tenant schema: {}", schemaName);
        createSchema(schemaName);
        updateTables(schemaName, "com.volter.shop");
    }

    private void updateTables(String schema, String basePackage) {
        log.info("Running schema update for package '{}' in schema '{}'", basePackage, schema);
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting(Environment.DATASOURCE, schemaDataSource(schema))
                .applySetting(Environment.PHYSICAL_NAMING_STRATEGY,
                        PhysicalNamingStrategySnakeCaseImpl.class.getName())
                .applySetting(Environment.HBM2DDL_AUTO, "update")
                .build();
        try {
            MetadataSources sources = new MetadataSources(registry);
            scanEntities(basePackage).forEach(sources::addAnnotatedClass);
            sources.buildMetadata().buildSessionFactory().close();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private DataSource schemaDataSource(String schema) {
        return new DelegatingDataSource(dataSource) {
            @Override
            public Connection getConnection() throws SQLException {
                Connection conn = dataSource.getConnection();
                conn.createStatement().execute("SET search_path TO " + schema);
                return conn;
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return getConnection();
            }
        };
    }

    private List<Class<?>> scanEntities(String basePackage) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        return scanner.findCandidateComponents(basePackage).stream()
                .map(bd -> {
                    try {
                        return Class.forName(bd.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<String> getShopSchemas() {
        List<String> schemas = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            // shop table might not exist yet on very first startup
            boolean shopExists = conn.getMetaData()
                    .getTables(null, "public", "shop", null).next();
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

    private void createSchema(String schema) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS \"" + schema + "\"");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create schema: " + schema, e);
        }
    }
}
