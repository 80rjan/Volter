package com.volter.shop.customer;

import com.volter.shop.modules.customer.web.response.CustomerResponse;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.domain.model.enums.CustomerRiskLevel;
import com.volter.shop.modules.customer.infrastructure.mapper.CustomerMapper;
import com.volter.shop.modules.customer.infrastructure.mapper.CustomerMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CustomerMapperImpl.class)
class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    // ─── fixture ─────────────────────────────────────────────────────────────

    private Customer customer() {
        return Customer.builder()
                .name("Alice Doe")
                .phoneNumber("070123456")
                .reservePhoneNumber("071234567")
                .embg("1234567890123")
                .address("Street 1")
                .city("Skopje")
                .riskLevel(CustomerRiskLevel.MEDIUM)
                .totalPawnCount(5)
                .totalSaleCount(2)
                .lateRenewalCount(1)
                .avgDaysLate(3.0)
                .onTimeRenewalCount(4)
                .forfeitCount(0)
                .redeemCount(3)
                .build();
    }

    // =========================================================================
    // toDTO() — identity fields
    // =========================================================================

    @Nested
    @DisplayName("toDTO() — identity fields")
    class IdentityFields {

        @Test
        @DisplayName("maps name")
        void mapsName() {
            assertThat(customerMapper.toDTO(customer()).getName()).isEqualTo("Alice Doe");
        }

        @Test
        @DisplayName("maps phoneNumber")
        void mapsPhoneNumber() {
            assertThat(customerMapper.toDTO(customer()).getPhoneNumber()).isEqualTo("070123456");
        }

        @Test
        @DisplayName("maps reservePhoneNumber")
        void mapsReservePhoneNumber() {
            assertThat(customerMapper.toDTO(customer()).getReservePhoneNumber()).isEqualTo("071234567");
        }

        @Test
        @DisplayName("maps embg")
        void mapsEmbg() {
            assertThat(customerMapper.toDTO(customer()).getEmbg()).isEqualTo("1234567890123");
        }

        @Test
        @DisplayName("maps address")
        void mapsAddress() {
            assertThat(customerMapper.toDTO(customer()).getAddress()).isEqualTo("Street 1");
        }

        @Test
        @DisplayName("maps city")
        void mapsCity() {
            assertThat(customerMapper.toDTO(customer()).getCity()).isEqualTo("Skopje");
        }
    }

    // =========================================================================
    // toDTO() — statistics & risk
    // =========================================================================

    @Nested
    @DisplayName("toDTO() — statistics and risk level")
    class StatsAndRisk {

        @Test
        @DisplayName("maps riskLevel")
        void mapsRiskLevel() {
            assertThat(customerMapper.toDTO(customer()).getRiskLevel()).isEqualTo(CustomerRiskLevel.MEDIUM);
        }

        @Test
        @DisplayName("maps all stat counters")
        void mapsStatCounters() {
            CustomerResponse r = customerMapper.toDTO(customer());
            assertThat(r.getTotalPawnCount()).isEqualTo(5);
            assertThat(r.getTotalSaleCount()).isEqualTo(2);
            assertThat(r.getLateRenewalCount()).isEqualTo(1);
            assertThat(r.getAvgDaysLate()).isEqualTo(3.0);
            assertThat(r.getOnTimeRenewalCount()).isEqualTo(4);
            assertThat(r.getForfeitCount()).isEqualTo(0);
            assertThat(r.getRedeemCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("default riskLevel (LOW) is mapped when not set")
        void defaultRiskLevel_isMapped() {
            Customer c = Customer.builder()
                    .name("Bob").phoneNumber("070000000").embg("9876543210987")
                    .address("St 2").city("Bitola").build();
            assertThat(customerMapper.toDTO(c).getRiskLevel()).isEqualTo(CustomerRiskLevel.LOW);
        }

        @Test
        @DisplayName("default stat counters are zero when not set")
        void defaultCounters_areZero() {
            Customer c = Customer.builder()
                    .name("Bob").phoneNumber("070000000").embg("9876543210987")
                    .address("St 2").city("Bitola").build();
            CustomerResponse r = customerMapper.toDTO(c);
            assertThat(r.getTotalPawnCount()).isZero();
            assertThat(r.getTotalSaleCount()).isZero();
            assertThat(r.getForfeitCount()).isZero();
        }
    }

    // =========================================================================
    // toDTO() — nullable fields
    // =========================================================================

    @Nested
    @DisplayName("toDTO() — nullable fields")
    class NullableFields {

        @Test
        @DisplayName("null reservePhoneNumber is mapped as null")
        void nullReservePhoneNumber_mapsAsNull() {
            Customer c = Customer.builder()
                    .name("Carol").phoneNumber("070111111").embg("1111111111111")
                    .address("St 3").city("Ohrid").build();
            assertThat(customerMapper.toDTO(c).getReservePhoneNumber()).isNull();
        }

        @Test
        @DisplayName("createdAt and updatedAt are null when @PrePersist has not fired")
        void timestamps_areNullWithoutPrePersist() {
            CustomerResponse r = customerMapper.toDTO(customer());
            assertThat(r.getCreatedAt()).isNull();
            assertThat(r.getUpdatedAt()).isNull();
        }
    }
}
