package com.volter.shop.it;

import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import com.volter.platform.modules.shop.domain.repository.ShopRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the whole harness: the multitenant Spring context boots against
 * Testcontainers Postgres and {@code SchemaInitializer}/Flyway ran the public
 * migrations (V1–V3), so the bootstrap staff and shop exist.
 */
class ContextSmokeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ShopRepository shopRepository;

    @Test
    void contextStartsAndBootstrapMigrationsRan() {
        assertThat(staffRepository.findByUsername("user")).isPresent();   // V3 seed staff
        assertThat(shopRepository.findByCode("TEST")).isPresent();        // V3 seed shop
    }
}
