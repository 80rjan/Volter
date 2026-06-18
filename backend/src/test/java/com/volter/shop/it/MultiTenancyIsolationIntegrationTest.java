package com.volter.shop.it;

import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import com.volter.platform.modules.shop.domain.repository.ShopRepository;
import com.volter.shared.config.SchemaInitializer;
import com.volter.shared.multitenancy.TenantContext;
import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves schema-per-tenant isolation end to end against a real PostgreSQL:
 * <ul>
 *   <li>a tenant-scoped entity ({@link CashRegister}, {@code @Table} with no schema) written under
 *       one tenant is invisible from another, because Hibernate routes to {@code SET search_path TO
 *       &lt;tenant&gt;, public} per {@code SchemaConnectionProvider};</li>
 *   <li>public-pinned entities ({@code Shop}, {@code Staff} — {@code @Table(schema="public")}) are
 *       visible from every tenant;</li>
 *   <li>{@code SchemaInitializer.initializeTenantSchema} provisions a brand-new shop schema on demand.</li>
 * </ul>
 *
 * Two throwaway schemas are provisioned per test and dropped in teardown, so the (persistent,
 * possibly reused) external database stays clean and the assertions are deterministic across runs.
 */
class MultiTenancyIsolationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CashRegisterRepository cashRegisterRepository; // tenant-scoped
    @Autowired
    private ShopRepository shopRepository;                 // public-pinned
    @Autowired
    private StaffRepository staffRepository;               // public-pinned
    @Autowired
    private SchemaInitializer schemaInitializer;
    @Autowired
    private DataSource dataSource;

    private String tenantA;
    private String tenantB;

    @BeforeEach
    void provisionTenants() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        tenantA = "shop_it_a_" + suffix;
        tenantB = "shop_it_b_" + suffix;
        schemaInitializer.initializeTenantSchema(tenantA);
        schemaInitializer.initializeTenantSchema(tenantB);
    }

    @AfterEach
    void dropTenants() {
        TenantContext.clear();
        dropSchema(tenantA);
        dropSchema(tenantB);
    }

    @Test
    void tenantScopedRowsAreIsolatedPerSchema() {
        // Write a register into tenant A only.
        withTenant(tenantA, () -> cashRegisterRepository.save(CashRegister.create("REG-A")));

        // Tenant B must not see tenant A's data, and gets its own independent register.
        assertThat(withTenant(tenantB, () -> cashRegisterRepository.existsByCode("REG-A"))).isFalse();
        withTenant(tenantB, () -> cashRegisterRepository.save(CashRegister.create("REG-B")));

        // Each tenant sees only its own row.
        assertThat(withTenant(tenantA, () -> cashRegisterRepository.existsByCode("REG-A"))).isTrue();
        assertThat(withTenant(tenantA, () -> cashRegisterRepository.existsByCode("REG-B"))).isFalse();
        assertThat(withTenant(tenantB, () -> cashRegisterRepository.existsByCode("REG-B"))).isTrue();
        assertThat(withTenant(tenantB, () -> cashRegisterRepository.existsByCode("REG-A"))).isFalse();

        assertThat(withTenant(tenantA, () -> cashRegisterRepository.count())).isEqualTo(1L);
        assertThat(withTenant(tenantB, () -> cashRegisterRepository.count())).isEqualTo(1L);
    }

    @Test
    void publicPinnedEntitiesAreVisibleFromEveryTenant() {
        // Bootstrap seed lives in public.shop / public.staff and must resolve regardless of tenant.
        assertThat(withTenant(tenantA, () -> shopRepository.findByCode("TEST"))).isPresent();
        assertThat(withTenant(tenantB, () -> shopRepository.findByCode("TEST"))).isPresent();
        assertThat(withTenant(tenantA, () -> staffRepository.findByUsername("user"))).isPresent();
        assertThat(withTenant(tenantB, () -> staffRepository.findByUsername("user"))).isPresent();
    }

    // ----- helpers -----

    /** Sets the tenant for the duration of the supplier call (each repo call opens its own session). */
    private <T> T withTenant(String schema, Supplier<T> action) {
        TenantContext.setCurrentTenant(schema);
        try {
            return action.get();
        } finally {
            TenantContext.clear();
        }
    }

    private void withTenant(String schema, Runnable action) {
        withTenant(schema, () -> {
            action.run();
            return null;
        });
    }

    private void dropSchema(String schema) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to drop test schema " + schema, e);
        }
    }
}
