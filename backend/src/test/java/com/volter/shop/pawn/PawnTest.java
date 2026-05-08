package com.volter.shop.pawn;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.pawn.web.request.PawnCreationRequest;
import com.volter.shop.modules.pawn.web.request.PawnModificationRequest;
import com.volter.shop.modules.pawn.application.dto.PawnModificationResult;
import com.volter.shop.modules.pawn.application.dto.PawnRedemptionResult;
import com.volter.shop.modules.pawn.application.dto.PawnRenewalResult;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.model.enums.PawnStatus;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.pawn.domain.model.event.*;
import com.volter.shop.modules.pawn.domain.model.valueobject.PawnPeriod;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    @Mock
    private Customer customer;
    @Mock
    private Item item;
    @Mock
    private Staff staff;
    @Mock
    private CashRegisterSession cashRegisterSession;

    private Pawn pawn;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        pawn = Pawn.builder()
                .amount(new Money(10000))
                .interest(new Money(1000))
                .period(new PawnPeriod(LocalDate.now(), LocalDate.now().plusDays(30)))
                .defaultDurationDays(30)
                .status(PawnStatus.ACTIVE)
                .active(true)
                .customer(customer)
                .item(item)
                .build();
    }


    @Test
    @DisplayName("getInitialTransaction() should throw when no creation transaction exists")
    void testGetInitialTransaction_ThrowsWhenMissing() {
        assertThrows(IllegalStateException.class, () -> pawn.getInitialTransaction());
    }

    // ========== CREATE TESTS ==========

    @Test
    @DisplayName("create() should initialize pawn with ACTIVE status and active=true")
    void testCreate_InitializesWithActiveStatus() {
        Pawn created = Pawn.create(buildCreationRequest(5000, 500, 30), cashRegisterSession, staff, item, customer);

        assertEquals(PawnStatus.ACTIVE, created.getStatus());
        assertTrue(created.isActive());
    }

    @Test
    @DisplayName("create() should set amount and interest from request")
    void testCreate_SetsAmountAndInterest() {
        Pawn created = Pawn.create(buildCreationRequest(8000, 800, 30), cashRegisterSession, staff, item, customer);

        assertEquals(8000, created.getAmount().amount());
        assertEquals(800, created.getInterest().amount());
    }

    @Test
    @DisplayName("create() should set issue/maturity dates and duration from request")
    void testCreate_SetsDatesAndDuration() {
        LocalDate issue = LocalDate.of(2025, 1, 1);
        LocalDate maturity = LocalDate.of(2025, 1, 31);

        PawnCreationRequest req = buildCreationRequest(5000, 500, 30);
        req.setIssueDate(issue);
        req.setMaturityDate(maturity);

        Pawn created = Pawn.create(req, cashRegisterSession, staff, item, customer);

        assertEquals(issue, created.getPeriod().issueDate());
        assertEquals(maturity, created.getPeriod().maturityDate());
        assertEquals(30, created.getDefaultDurationDays());
    }

    @Test
    @DisplayName("create() should assign the given customer and item")
    void testCreate_SetsCustomerAndItem() {
        Pawn created = Pawn.create(buildCreationRequest(5000, 500, 30), cashRegisterSession, staff, item, customer);

        assertEquals(customer, created.getCustomer());
        assertEquals(item, created.getItem());
    }

    @Test
    @DisplayName("create() should add exactly one CREATION transaction")
    void testCreate_AddsCreationTransaction() {
        Pawn created = Pawn.create(buildCreationRequest(5000, 500, 30), cashRegisterSession, staff, item, customer);

        assertEquals(1, created.getTransactions().size());
        PawnTransaction tx = created.getTransactions().get(0);
        assertEquals(PawnTransactionAction.CREATION, tx.getAction());
        assertEquals(5000, tx.getAmount().amount());
        assertEquals(TransactionDirection.OUT, tx.getDirection());
        assertEquals(0, tx.getMarginAmount().amount());
        assertEquals(TransactionMarginType.NEUTRAL, tx.getMarginType());
        assertEquals(staff, tx.getStaff());
        assertEquals(cashRegisterSession, tx.getCashRegisterSession());
    }

    @Test
    @DisplayName("create() transaction description from request is set on transaction")
    void testCreate_TransactionDescriptionIsSet() {
        PawnCreationRequest req = buildCreationRequest(5000, 500, 30);
        req.setTransactionDescription("Initial loan for gold ring");

        Pawn created = Pawn.create(req, cashRegisterSession, staff, item, customer);

        assertEquals("Initial loan for gold ring", created.getTransactions().get(0).getDescription());
    }

    @Test
    @DisplayName("create() should add exactly one PawnCreatedEvent")
    void testCreate_AddsCreatedEvent() {
        Pawn created = Pawn.create(buildCreationRequest(5000, 500, 30), cashRegisterSession, staff, item, customer);

        assertEquals(1, created.getPawnEvents().size());
        assertTrue(created.getPawnEvents().get(0) instanceof PawnCreatedEvent);
        assertEquals(staff, ((PawnCreatedEvent) created.getPawnEvents().get(0)).getPerformedBy());
    }

    @Test
    @DisplayName("create() transaction is retrievable via getInitialTransaction()")
    void testCreate_GetInitialTransactionReturnsCreationTx() {
        Pawn created = Pawn.create(buildCreationRequest(5000, 500, 30), cashRegisterSession, staff, item, customer);

        PawnTransaction initial = created.getInitialTransaction();
        assertEquals(PawnTransactionAction.CREATION, initial.getAction());
    }

    // ========== RENEW TESTS ==========

    @Test
    @DisplayName("renew() should extend maturity date correctly")
    void testRenew_ExtendsMaturityDateCorrectly() {
        LocalDate oldMaturityDate = pawn.getPeriod().maturityDate();
        Money paidInterest = new Money(500); // 500 / (1000/30) = 15 days

        PawnRenewalResult result = pawn.renew(paidInterest, "Renewal payment", cashRegisterSession, staff);

        LocalDate expectedNewDate = oldMaturityDate.plusDays(15);
        assertEquals(expectedNewDate, pawn.getPeriod().maturityDate());
    }

    @ParameterizedTest
    @CsvSource({
            "1000, 30",  // Full interest = full period
            "500, 15",   // Half interest = half period
            "333, 10",    // 1/3 interest ≈ 1/3 period (9.99 → 10)
            "100, 3"     // Small payment
    })
    @DisplayName("renew() should calculate extension days correctly")
    void testRenew_CalculatesExtensionDays(int paidAmount, int expectedDays) {
        LocalDate oldMaturityDate = pawn.getPeriod().maturityDate();
        Money paidInterest = new Money(paidAmount);

        pawn.renew(paidInterest, "Renewal", cashRegisterSession, staff);

        LocalDate expectedNewDate = oldMaturityDate.plusDays(expectedDays);
        assertEquals(expectedNewDate, pawn.getPeriod().maturityDate());
    }

    @Test
    @DisplayName("renew() should add renewal transaction with correct details")
    void testRenew_AddsRenewalTransaction() {
        Money paidInterest = new Money(500);

        pawn.renew(paidInterest, "Renewal payment", cashRegisterSession, staff);

        assertEquals(1, pawn.getTransactions().size());
        PawnTransaction tx = pawn.getTransactions().get(0);
        assertEquals(PawnTransactionAction.RENEWAL, tx.getAction());
        assertEquals(500, tx.getAmount().amount());
        assertEquals(TransactionDirection.IN, tx.getDirection());
        assertEquals(500, tx.getMarginAmount().amount());
        assertEquals(TransactionMarginType.PROFIT, tx.getMarginType());
        assertEquals("Renewal payment", tx.getDescription());
        assertEquals(staff, tx.getStaff());
        assertEquals(cashRegisterSession, tx.getCashRegisterSession());
    }

    @Test
    @DisplayName("renew() should add renewal event with maturity date change")
    void testRenew_AddsRenewalEvent() {
        LocalDate oldMaturityDate = pawn.getPeriod().maturityDate();
        Money paidInterest = new Money(500);

        pawn.renew(paidInterest, "Renewal", cashRegisterSession, staff);

        assertEquals(1, pawn.getPawnEvents().size());
        PawnEvent event = pawn.getPawnEvents().get(0);
        assertTrue(event instanceof PawnRenewedEvent);

        PawnRenewedEvent renewEvent = (PawnRenewedEvent) event;
        assertEquals(oldMaturityDate, renewEvent.getMaturityDateChange().oldMaturityDate());
        assertEquals(oldMaturityDate.plusDays(15), renewEvent.getMaturityDateChange().newMaturityDate());
        assertEquals(500, renewEvent.getInterestPaid().amount());
        assertEquals(staff, renewEvent.getPerformedBy());
    }

    @Test
    @DisplayName("renew() should return result with transaction")
    void testRenew_ReturnsCorrectResult() {
        Money paidInterest = new Money(500);

        PawnRenewalResult result = pawn.renew(paidInterest, "Renewal", cashRegisterSession, staff);

        assertNotNull(result);
        assertNotNull(result.transaction());
        assertEquals(PawnTransactionAction.RENEWAL, result.transaction().getAction());
    }

    @Test
    @DisplayName("renew() with zero interest should extend zero days")
    void testRenew_ZeroInterest() {
        LocalDate oldMaturityDate = pawn.getPeriod().maturityDate();
        Money paidInterest = new Money(0);

        pawn.renew(paidInterest, "Zero renewal", cashRegisterSession, staff);

        assertEquals(oldMaturityDate, pawn.getPeriod().maturityDate());
    }

    // ========== REDEEM TESTS ==========

    @Test
    @DisplayName("redeem() should set status to REDEEMED and inactive")
    void testRedeem_SetsStatusAndInactive() {
        Money paidAmount = new Money(11000); // amount + interest

        pawn.redeem(paidAmount, "Full redemption", cashRegisterSession, staff);

        assertEquals(PawnStatus.REDEEMED, pawn.getStatus());
        assertFalse(pawn.isActive());
    }

    @Test
    @DisplayName("redeem() with exact amount should create zero-margin transaction")
    void testRedeem_ExactAmount() {
        Money exactAmount = new Money(11000); // 10000 + 1000

        PawnRedemptionResult result = pawn.redeem(exactAmount, "Exact redemption", cashRegisterSession, staff);

        PawnTransaction tx = pawn.getTransactions().get(0);
        assertEquals(PawnTransactionAction.REDEMPTION, tx.getAction());
        assertEquals(11000, tx.getAmount().amount());
        assertEquals(TransactionDirection.IN, tx.getDirection());
        assertEquals(1000, tx.getMarginAmount().amount()); // absoluteSubtract(amount)
        assertEquals(TransactionMarginType.LOSS, tx.getMarginType()); // Not profit since equal
        assertFalse(result.underpaid());
    }

    @Test
    @DisplayName("redeem() with overpayment should mark as PROFIT")
    void testRedeem_Overpayment() {
        Money overpayment = new Money(12000); // 10000 + 1000 + 1000 extra

        PawnRedemptionResult result = pawn.redeem(overpayment, "Overpayment", cashRegisterSession, staff);

        PawnTransaction tx = pawn.getTransactions().get(0);
        assertEquals(2000, tx.getMarginAmount().amount()); // 12000 - 10000
        assertEquals(TransactionMarginType.PROFIT, tx.getMarginType());
        assertFalse(result.underpaid());
    }

    @Test
    @DisplayName("redeem() with underpayment should mark as LOSS and flag underpaid")
    void testRedeem_Underpayment() {
        Money underpayment = new Money(10000); // Only principal, no interest

        PawnRedemptionResult result = pawn.redeem(underpayment, "Underpayment", cashRegisterSession, staff);

        PawnTransaction tx = pawn.getTransactions().get(0);
        assertEquals(0, tx.getMarginAmount().amount()); // 10000 - 10000
        assertEquals(TransactionMarginType.LOSS, tx.getMarginType());
        assertTrue(result.underpaid());
    }

    @Test
    @DisplayName("redeem() with partial payment should flag underpaid")
    void testRedeem_PartialPayment() {
        Money partialPayment = new Money(5000); // Less than principal

        PawnRedemptionResult result = pawn.redeem(partialPayment, "Partial", cashRegisterSession, staff);

        assertTrue(result.underpaid());
        assertEquals(TransactionMarginType.LOSS, pawn.getTransactions().get(0).getMarginType());
    }

    @Test
    @DisplayName("redeem() should add redemption event")
    void testRedeem_AddsRedemptionEvent() {
        Money paidAmount = new Money(11000);

        pawn.redeem(paidAmount, "Redemption", cashRegisterSession, staff);

        assertEquals(1, pawn.getPawnEvents().size());
        PawnEvent event = pawn.getPawnEvents().get(0);
        assertTrue(event instanceof PawnRedeemedEvent);

        PawnRedeemedEvent redeemEvent = (PawnRedeemedEvent) event;
        assertEquals(11000, redeemEvent.getTotalAmountPaid().amount());
        assertEquals(1000, redeemEvent.getInterestPaid().amount()); // 11000 - 10000
        assertEquals(staff, redeemEvent.getPerformedBy());
    }

    @Test
    @DisplayName("redeem() should return correct result")
    void testRedeem_ReturnsCorrectResult() {
        Money paidAmount = new Money(11000);

        PawnRedemptionResult result = pawn.redeem(paidAmount, "Redemption", cashRegisterSession, staff);

        assertNotNull(result);
        assertNotNull(result.transaction());
        assertNotNull(result.event());
        assertFalse(result.underpaid());
    }

    // ========== FORFEIT TESTS ==========

    @Test
    @DisplayName("forfeit() should set status to FORFEITED and inactive")
    void testForfeit_SetsStatusAndInactive() {
        pawn.forfeit("Forfeiture", cashRegisterSession, staff);

        assertEquals(PawnStatus.FORFEITED, pawn.getStatus());
        assertFalse(pawn.isActive());
    }

    @Test
    @DisplayName("forfeit() should add zero-amount neutral transaction")
    void testForfeit_AddsNeutralTransaction() {
        pawn.forfeit("Customer didn't return", cashRegisterSession, staff);

        assertEquals(1, pawn.getTransactions().size());
        PawnTransaction tx = pawn.getTransactions().get(0);
        assertEquals(PawnTransactionAction.FORFEITURE, tx.getAction());
        assertEquals(0, tx.getAmount().amount());
        assertEquals(TransactionDirection.NEUTRAL, tx.getDirection());
        assertEquals(0, tx.getMarginAmount().amount());
        assertEquals(TransactionMarginType.NEUTRAL, tx.getMarginType());
        assertEquals("Customer didn't return", tx.getDescription());
        assertEquals(staff, tx.getStaff());
        assertEquals(cashRegisterSession, tx.getCashRegisterSession());
    }

    @Test
    @DisplayName("forfeit() should add forfeit event with unpaid amounts")
    void testForfeit_AddsForfeitEvent() {
        pawn.forfeit("Forfeiture", cashRegisterSession, staff);

        assertEquals(1, pawn.getPawnEvents().size());
        PawnEvent event = pawn.getPawnEvents().get(0);
        assertTrue(event instanceof PawnForfeitedEvent);

        PawnForfeitedEvent forfeitEvent = (PawnForfeitedEvent) event;
        assertEquals(pawn.getPeriod().maturityDate(), forfeitEvent.getMaturityDate());
        assertEquals(10000, forfeitEvent.getUnpaidAmount().amount());
        assertEquals(1000, forfeitEvent.getUnpaidInterest().amount());
        assertEquals(staff, forfeitEvent.getPerformedBy());
    }

    // ========== MODIFY TESTS ==========

    @Test
    @DisplayName("modify() should update amount when provided")
    void testModify_UpdatesAmount() {
        PawnModificationRequest request = new PawnModificationRequest(12000, null, null, null, "Amount correction");

        pawn.modify(request, cashRegisterSession, staff);

        assertEquals(12000, pawn.getAmount().amount());
    }

    @Test
    @DisplayName("modify() should update interest when provided")
    void testModify_UpdatesInterest() {
        PawnModificationRequest request = new PawnModificationRequest(null, 1200, null, null, "Interest correction");

        pawn.modify(request, cashRegisterSession, staff);

        assertEquals(1200, pawn.getInterest().amount());
    }

    @Test
    @DisplayName("modify() should update duration when provided")
    void testModify_UpdatesDuration() {
        PawnModificationRequest request = new PawnModificationRequest(null, null, 45, null, "Duration correction");

        pawn.modify(request, cashRegisterSession, staff);

        assertEquals(45, pawn.getDefaultDurationDays());
    }

    @Test
    @DisplayName("modify() should update all fields when all provided")
    void testModify_UpdatesAllFields() {
        PawnModificationRequest request = new PawnModificationRequest(15000, 1500, 60, null, "Full correction");

        pawn.modify(request, cashRegisterSession, staff);

        assertEquals(15000, pawn.getAmount().amount());
        assertEquals(1500, pawn.getInterest().amount());
        assertEquals(60, pawn.getDefaultDurationDays());
    }

    @Test
    @DisplayName("modify() with increased amount should create OUT transaction")
    void testModify_IncreasedAmount() {
        PawnModificationRequest request = new PawnModificationRequest(12000, null, null, null, "Increase");

        PawnModificationResult result = pawn.modify(request, cashRegisterSession, staff);

        PawnTransaction tx = pawn.getTransactions().get(0);
        assertEquals(PawnTransactionAction.MODIFICATION, tx.getAction());
        assertEquals(2000, tx.getAmount().amount()); // |12000 - 10000|
        assertEquals(TransactionDirection.OUT, tx.getDirection());
        assertEquals(0, tx.getMarginAmount().amount());
        assertEquals(TransactionMarginType.NEUTRAL, tx.getMarginType());
        assertEquals(2000, result.amountDifference());
    }

    @Test
    @DisplayName("modify() with decreased amount should create IN transaction")
    void testModify_DecreasedAmount() {
        PawnModificationRequest request = new PawnModificationRequest(8000, null, null, null, "Decrease");

        PawnModificationResult result = pawn.modify(request, cashRegisterSession, staff);

        PawnTransaction tx = pawn.getTransactions().get(0);
        assertEquals(2000, tx.getAmount().amount()); // |8000 - 10000|
        assertEquals(TransactionDirection.IN, tx.getDirection());
        assertEquals(-2000, result.amountDifference());
    }

    @Test
    @DisplayName("modify() with no amount change should create zero-amount transaction")
    void testModify_NoAmountChange() {
        PawnModificationRequest request = new PawnModificationRequest(null, 1200, null, null, "Interest only");

        PawnModificationResult result = pawn.modify(request, cashRegisterSession, staff);

        PawnTransaction tx = pawn.getTransactions().get(0);
        assertEquals(0, tx.getAmount().amount());
        assertEquals(TransactionDirection.NEUTRAL, tx.getDirection()); // Default for 0 difference
        assertEquals(0, result.amountDifference());
    }

    @Test
    @DisplayName("modify() should add modification event with snapshots")
    void testModify_AddsModificationEvent() {
        PawnModificationRequest request = new PawnModificationRequest(12000, 1200, 45, null, "Full update");

        pawn.modify(request, cashRegisterSession, staff);

        assertEquals(1, pawn.getPawnEvents().size());
        PawnEvent event = pawn.getPawnEvents().get(0);
        assertTrue(event instanceof PawnModifiedEvent);

        PawnModifiedEvent modifyEvent = (PawnModifiedEvent) event;

        // Previous snapshot
        assertEquals(10000, modifyEvent.getPreviousSnapshot().getAmount());
        assertEquals(1000, modifyEvent.getPreviousSnapshot().getInterest());
        assertEquals(30, modifyEvent.getPreviousSnapshot().getDefaultDurationDays());

        // New snapshot
        assertEquals(12000, modifyEvent.getNewSnapshot().getAmount());
        assertEquals(1200, modifyEvent.getNewSnapshot().getInterest());
        assertEquals(45, modifyEvent.getNewSnapshot().getDefaultDurationDays());

        assertEquals(staff, modifyEvent.getPerformedBy());
    }

    @Test
    @DisplayName("modify() should return correct result")
    void testModify_ReturnsCorrectResult() {
        PawnModificationRequest request = new PawnModificationRequest(12000, null, null, null, "Update");

        PawnModificationResult result = pawn.modify(request, cashRegisterSession, staff);

        assertNotNull(result);
        assertNotNull(result.transaction());
        assertNotNull(result.event());
        assertEquals(2000, result.amountDifference());
    }

    // ========== INTEGRATION/MULTI-STEP TESTS ==========

    @Test
    @DisplayName("Multiple renewals should accumulate correctly")
    void testMultipleRenewals() {
        LocalDate originalMaturity = pawn.getPeriod().maturityDate();

        pawn.renew(new Money(500), "First renewal", cashRegisterSession, staff);
        pawn.renew(new Money(500), "Second renewal", cashRegisterSession, staff);

        LocalDate expectedMaturity = originalMaturity.plusDays(15 + 15);
        assertEquals(expectedMaturity, pawn.getPeriod().maturityDate());
        assertEquals(2, pawn.getTransactions().size());
        assertEquals(2, pawn.getPawnEvents().size());
    }

    @Test
    @DisplayName("Modify then renew should use updated interest for calculation")
    void testModifyThenRenew() {
        // Modify interest from 1000 to 2000
        pawn.modify(new PawnModificationRequest(null, 2000, null, null, "Update"), cashRegisterSession, staff);

        LocalDate beforeRenew = pawn.getPeriod().maturityDate();

        // Pay 2000 interest (now should give full 30 days)
        pawn.renew(new Money(2000), "Renewal", cashRegisterSession, staff);

        LocalDate expectedMaturity = beforeRenew.plusDays(30);
        assertEquals(expectedMaturity, pawn.getPeriod().maturityDate());
    }

    @Test
    @DisplayName("Renew then redeem should preserve all history")
    void testRenewThenRedeem() {
        pawn.renew(new Money(500), "Renewal", cashRegisterSession, staff);
        pawn.redeem(new Money(11000), "Redemption", cashRegisterSession, staff);

        assertEquals(2, pawn.getTransactions().size());
        assertEquals(2, pawn.getPawnEvents().size());
        assertEquals(PawnStatus.REDEEMED, pawn.getStatus());

        assertEquals(PawnTransactionAction.RENEWAL, pawn.getTransactions().get(0).getAction());
        assertEquals(PawnTransactionAction.REDEMPTION, pawn.getTransactions().get(1).getAction());
    }

    // ========== HELPER METHODS ==========

    private PawnCreationRequest buildCreationRequest(int amount, int interest, int durationDays) {
        PawnCreationRequest req = new PawnCreationRequest();
        req.setAmount(amount);
        req.setInterest(interest);
        req.setIssueDate(LocalDate.now());
        req.setMaturityDate(LocalDate.now().plusDays(durationDays));
        req.setDurationDays(durationDays);
        return req;
    }
}