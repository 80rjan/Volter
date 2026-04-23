package com.volter.shop.customer;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.domain.model.enums.CustomerRiskLevel;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .name("Test IdentityUser")
                .phoneNumber("070123456")
                .embg("1234567890123")
                .address("Test St 1")
                .city("Skopje")
                .build();
    }

    // ─── pawnAction – CREATION ────────────────────────────────────────────────

    @Test
    void pawnAction_creation_incrementsTotalPawnCount() {
        customer.pawnAction(PawnTransactionAction.CREATION);
        assertThat(customer.getTotalPawnCount()).isEqualTo(1);
    }

    @Test
    void pawnAction_creation_multipleTimesAccumulates() {
        customer.pawnAction(PawnTransactionAction.CREATION);
        customer.pawnAction(PawnTransactionAction.CREATION);
        assertThat(customer.getTotalPawnCount()).isEqualTo(2);
    }

    // ─── pawnAction – RENEWAL on time ────────────────────────────────────────

    @Test
    void pawnAction_renewalOnTime_incrementsOnTimeCount() {
        customer.pawnAction(PawnTransactionAction.RENEWAL, 0L);
        assertThat(customer.getOnTimeRenewalCount()).isEqualTo(1);
        assertThat(customer.getLateRenewalCount()).isZero();
    }

    @Test
    void pawnAction_renewalNegativeDays_treatedAsOnTime() {
        customer.pawnAction(PawnTransactionAction.RENEWAL, -3L);
        assertThat(customer.getOnTimeRenewalCount()).isEqualTo(1);
        assertThat(customer.getLateRenewalCount()).isZero();
    }

    // ─── pawnAction – RENEWAL late ───────────────────────────────────────────

    @Test
    void pawnAction_renewalLate_incrementsLateCount() {
        customer.pawnAction(PawnTransactionAction.RENEWAL, 5L);
        assertThat(customer.getLateRenewalCount()).isEqualTo(1);
        assertThat(customer.getOnTimeRenewalCount()).isZero();
    }

    @Test
    void pawnAction_renewalLate_firstEntry_avgDaysLateEqualsValue() {
        customer.pawnAction(PawnTransactionAction.RENEWAL, 6L);
        assertThat(customer.getAvgDaysLate()).isEqualTo(6.0);
    }

    @Test
    void pawnAction_renewalLate_twoEntries_avgDaysLateIsCorrect() {
        customer.pawnAction(PawnTransactionAction.RENEWAL, 4L);
        customer.pawnAction(PawnTransactionAction.RENEWAL, 8L);
        // avg = (4 + 8) / 2 = 6.0
        assertThat(customer.getAvgDaysLate()).isEqualTo(6.0);
    }

    @Test
    void pawnAction_renewalLate_threeEntries_avgDaysLateIsCorrect() {
        customer.pawnAction(PawnTransactionAction.RENEWAL, 3L);
        customer.pawnAction(PawnTransactionAction.RENEWAL, 6L);
        customer.pawnAction(PawnTransactionAction.RENEWAL, 9L);
        // avg = (3 + 6 + 9) / 3 = 6.0
        assertThat(customer.getAvgDaysLate()).isEqualTo(6.0);
    }

    @Test
    void pawnAction_renewalMixed_avgOnlyCountsLateEntries() {
        customer.pawnAction(PawnTransactionAction.RENEWAL, 0L);  // on time
        customer.pawnAction(PawnTransactionAction.RENEWAL, 0L);  // on time
        customer.pawnAction(PawnTransactionAction.RENEWAL, 10L); // late
        // avg = 10 / 1 = 10.0
        assertThat(customer.getAvgDaysLate()).isEqualTo(10.0);
        assertThat(customer.getOnTimeRenewalCount()).isEqualTo(2);
        assertThat(customer.getLateRenewalCount()).isEqualTo(1);
    }

    // ─── pawnAction – RENEWAL null daysLate ──────────────────────────────────

    @Test
    void pawnAction_renewalWithNullDaysLate_throwsIllegalArgument() {
        assertThatThrownBy(() -> customer.pawnAction(PawnTransactionAction.RENEWAL, (Long) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Days late must be provided");
    }

    @Test
    void pawnAction_renewalSingleArgOverload_throwsIllegalArgument() {
        // single-arg overload passes null internally → must throw
        assertThatThrownBy(() -> customer.pawnAction(PawnTransactionAction.RENEWAL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ─── pawnAction – REDEMPTION ──────────────────────────────────────────────

    @Test
    void pawnAction_redemption_incrementsRedeemCount() {
        customer.pawnAction(PawnTransactionAction.REDEMPTION);
        assertThat(customer.getRedeemCount()).isEqualTo(1);
    }

    // ─── pawnAction – FORFEITURE ──────────────────────────────────────────────

    @Test
    void pawnAction_forfeiture_incrementsForfeitCount() {
        customer.pawnAction(PawnTransactionAction.FORFEITURE);
        assertThat(customer.getForfeitCount()).isEqualTo(1);
    }

    // ─── saleAction ───────────────────────────────────────────────────────────

    @Test
    void saleAction_incrementsTotalSaleCount() {
        customer.saleAction();
        assertThat(customer.getTotalSaleCount()).isEqualTo(1);
    }

    @Test
    void saleAction_doesNotAffectPawnCounters() {
        customer.saleAction();
        assertThat(customer.getTotalPawnCount()).isZero();
        assertThat(customer.getForfeitCount()).isZero();
    }

    // ─── calculateRiskLevel (via preUpdate) ──────────────────────────────────

    @Test
    void riskLevel_noPawns_isLow() {
        customer.preUpdate();
        assertThat(customer.getRiskLevel()).isEqualTo(CustomerRiskLevel.LOW);
    }

    @Test
    void riskLevel_low_withGoodHistory() {
        // 10 pawns, 1 late, avg 2 days → lateRate=0.1, forfeitRate=0.0
        repeat(10, () -> customer.pawnAction(PawnTransactionAction.CREATION));
        customer.pawnAction(PawnTransactionAction.RENEWAL, 2L);
        customer.preUpdate();
        assertThat(customer.getRiskLevel()).isEqualTo(CustomerRiskLevel.LOW);
    }

    @Test
    void riskLevel_medium_forfeitRateAbove20Percent() {
        // 10 pawns, 3 forfeits → forfeitRate = 0.3 > 0.2
        repeat(10, () -> customer.pawnAction(PawnTransactionAction.CREATION));
        repeat(3, () -> customer.pawnAction(PawnTransactionAction.FORFEITURE));
        customer.preUpdate();
        assertThat(customer.getRiskLevel()).isEqualTo(CustomerRiskLevel.MEDIUM);
    }

    @Test
    void riskLevel_medium_lateRateAbove30Percent() {
        // 10 pawns, 4 late renewals (rate=0.4), avg 3 days
        repeat(10, () -> customer.pawnAction(PawnTransactionAction.CREATION));
        repeat(4, () -> customer.pawnAction(PawnTransactionAction.RENEWAL, 3L));
        customer.preUpdate();
        assertThat(customer.getRiskLevel()).isEqualTo(CustomerRiskLevel.MEDIUM);
    }

    @Test
    void riskLevel_medium_avgDaysLateAbove5() {
        // 10 pawns, 1 late renewal with 6 days → lateRate=0.1 but avgDaysLate > 5
        repeat(10, () -> customer.pawnAction(PawnTransactionAction.CREATION));
        customer.pawnAction(PawnTransactionAction.RENEWAL, 6L);
        customer.preUpdate();
        assertThat(customer.getRiskLevel()).isEqualTo(CustomerRiskLevel.MEDIUM);
    }

    @Test
    void riskLevel_high_forfeitRateAbove60Percent() {
        // 10 pawns, 7 forfeits → forfeitRate = 0.7 > 0.6
        repeat(10, () -> customer.pawnAction(PawnTransactionAction.CREATION));
        repeat(7, () -> customer.pawnAction(PawnTransactionAction.FORFEITURE));
        customer.preUpdate();
        assertThat(customer.getRiskLevel()).isEqualTo(CustomerRiskLevel.HIGH);
    }

    @Test
    void riskLevel_high_highLateRateAndHighAvgDays() {
        // 10 pawns, 7 late renewals (rate=0.7 > 0.6), avg days = 15 > 10
        repeat(10, () -> customer.pawnAction(PawnTransactionAction.CREATION));
        repeat(7, () -> customer.pawnAction(PawnTransactionAction.RENEWAL, 15L));
        customer.preUpdate();
        assertThat(customer.getRiskLevel()).isEqualTo(CustomerRiskLevel.HIGH);
    }

    @Test
    void riskLevel_notHigh_whenLateRateHighButAvgDaysLow() {
        // lateRate > 0.6 but avgDaysLate <= 10 → should NOT be HIGH (MEDIUM instead)
        repeat(10, () -> customer.pawnAction(PawnTransactionAction.CREATION));
        repeat(7, () -> customer.pawnAction(PawnTransactionAction.RENEWAL, 5L));
        customer.preUpdate();
        assertThat(customer.getRiskLevel()).isNotEqualTo(CustomerRiskLevel.HIGH);
    }

    @Test
    void riskLevel_high_forfeitRateExactly60PercentsIsNotHigh() {
        // forfeitRate = 0.6, threshold is strictly > 0.6 → should be MEDIUM
        repeat(10, () -> customer.pawnAction(PawnTransactionAction.CREATION));
        repeat(6, () -> customer.pawnAction(PawnTransactionAction.FORFEITURE));
        customer.preUpdate();
        assertThat(customer.getRiskLevel()).isEqualTo(CustomerRiskLevel.MEDIUM);
    }

    // ─── helper ───────────────────────────────────────────────────────────────

    private void repeat(int times, Runnable action) {
        for (int i = 0; i < times; i++) action.run();
    }
}
