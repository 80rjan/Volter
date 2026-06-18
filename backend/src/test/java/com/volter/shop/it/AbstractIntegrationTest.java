package com.volter.shop.it;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base for integration tests: boots the full Spring context against a real
 * PostgreSQL. Flyway runs on startup via {@code SchemaInitializer}, so the public
 * schema and the bootstrap "Test Shop" (schema {@code shop_test}) tenant are
 * available — including the seed staff {@code user}/{@code password} (SUPER_ADMIN).
 *
 * <p>Database source:
 * <ul>
 *   <li><b>Default</b> — a throwaway Postgres managed by Testcontainers (just run
 *       {@code ./mvnw test}; Docker required).</li>
 *   <li><b>External override</b> — set {@code IT_DATASOURCE_URL} (and optionally
 *       {@code IT_DATASOURCE_USERNAME}/{@code IT_DATASOURCE_PASSWORD}) to point at an
 *       already-running Postgres. Useful where the Docker API client can't reach the
 *       daemon but a DB is otherwise reachable over JDBC.</li>
 * </ul>
 *
 * Lives under {@code com.volter.shop} so {@code @SpringBootTest} discovers
 * {@code VolterApplication} as its configuration.
 */
@SpringBootTest
public abstract class AbstractIntegrationTest {

    private static final String EXTERNAL_URL = System.getenv("IT_DATASOURCE_URL");
    static final PostgreSQLContainer<?> POSTGRES;

    static {
        if (EXTERNAL_URL == null) {
            POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
            POSTGRES.start();
        } else {
            POSTGRES = null;
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        if (POSTGRES != null) {
            registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES::getUsername);
            registry.add("spring.datasource.password", POSTGRES::getPassword);
        } else {
            registry.add("spring.datasource.url", () -> EXTERNAL_URL);
            registry.add("spring.datasource.username", () -> envOrDefault("IT_DATASOURCE_USERNAME", "test"));
            registry.add("spring.datasource.password", () -> envOrDefault("IT_DATASOURCE_PASSWORD", "test"));
        }
        registry.add("jwt.secret", () -> "integration-test-secret-key-at-least-32-bytes!!");
    }

    private static String envOrDefault(String key, String fallback) {
        String v = System.getenv(key);
        return v == null ? fallback : v;
    }
}
