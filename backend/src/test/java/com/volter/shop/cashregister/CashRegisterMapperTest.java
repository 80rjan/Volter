package com.volter.shop.cashregister;

import com.volter.shop.modules.cashregister.web.response.CashRegisterSessionResponse;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.infrastructure.mapper.CashRegisterMapper;
import com.volter.shop.modules.cashregister.infrastructure.mapper.CashRegisterMapperImpl;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CashRegisterMapperImpl.class)
class CashRegisterMapperTest {

    @Autowired
    private CashRegisterMapper cashRegisterMapper;

    // ─── fixture ─────────────────────────────────────────────────────────────

    private CashRegisterSession session() {
        return CashRegisterSession.builder()
                .openingBalance(new Money(5000))
                .currentBalance(new Money(4800))
                .expectedPawnInterest(new Money(200))
                .build();
    }

    // =========================================================================
    // toSessionResponse() — Money → Integer mapping
    // =========================================================================

    @Nested
    @DisplayName("toSessionResponse() — Money to Integer mapping")
    class MoneyMapping {

        @Test
        @DisplayName("maps openingBalance from Money to Integer")
        void mapsOpeningBalance() {
            assertThat(cashRegisterMapper.toSessionResponse(session()).openingBalance()).isEqualTo(5000);
        }

        @Test
        @DisplayName("maps currentBalance from Money to Integer")
        void mapsCurrentBalance() {
            assertThat(cashRegisterMapper.toSessionResponse(session()).currentBalance()).isEqualTo(4800);
        }

        @Test
        @DisplayName("maps expectedPawnInterest from Money to Integer")
        void mapsExpectedPawnInterest() {
            assertThat(cashRegisterMapper.toSessionResponse(session()).expectedPawnInterest()).isEqualTo(200);
        }

        @Test
        @DisplayName("null Money maps to null Integer")
        void nullMoney_mapsToNull() {
            CashRegisterSession s = CashRegisterSession.builder()
                    .openingBalance(null)
                    .currentBalance(new Money(0))
                    .expectedPawnInterest(new Money(0))
                    .build();
            assertThat(cashRegisterMapper.toSessionResponse(s).openingBalance()).isNull();
        }

        @Test
        @DisplayName("zero balance maps correctly")
        void zeroBalance_mapsCorrectly() {
            CashRegisterSession s = CashRegisterSession.builder()
                    .openingBalance(new Money(0))
                    .currentBalance(new Money(0))
                    .expectedPawnInterest(new Money(0))
                    .build();
            assertThat(cashRegisterMapper.toSessionResponse(s).openingBalance()).isZero();
            assertThat(cashRegisterMapper.toSessionResponse(s).currentBalance()).isZero();
            assertThat(cashRegisterMapper.toSessionResponse(s).expectedPawnInterest()).isZero();
        }
    }

    // =========================================================================
    // toSessionResponse() — timestamp behaviour
    // =========================================================================

    @Nested
    @DisplayName("toSessionResponse() — timestamps")
    class Timestamps {

        @Test
        @DisplayName("openedAt and updatedAt are null when @PrePersist has not fired")
        void timestamps_areNullWithoutPrePersist() {
            CashRegisterSessionResponse r = cashRegisterMapper.toSessionResponse(session());
            assertThat(r.openedAt()).isNull();
            assertThat(r.updatedAt()).isNull();
        }
    }

    // =========================================================================
    // toSessionResponse(List) — list delegation
    // =========================================================================

    @Nested
    @DisplayName("toSessionResponse(List)")
    class ListMapping {

        @Test
        @DisplayName("maps list of sessions — correct size")
        void mapsListCorrectly() {
            List<CashRegisterSessionResponse> results =
                    cashRegisterMapper.toSessionResponse(List.of(session(), session()));
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("empty list returns empty list")
        void emptyList_returnsEmptyList() {
            assertThat(cashRegisterMapper.toSessionResponse(List.of())).isEmpty();
        }

        @Test
        @DisplayName("each element is mapped correctly")
        void eachElement_isMappedCorrectly() {
            List<CashRegisterSessionResponse> results =
                    cashRegisterMapper.toSessionResponse(List.of(session()));
            assertThat(results.get(0).openingBalance()).isEqualTo(5000);
            assertThat(results.get(0).currentBalance()).isEqualTo(4800);
        }
    }
}
