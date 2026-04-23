package com.volter.shop.cashregister;

import com.volter.shop.modules.cashregister.application.dto.result.CashRegisterSessionCloseResult;
import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterTransaction;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyType;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CashRegisterSessionTest {

    private CashRegister cashRegister;
    private Staff staff;
    private CashRegisterSession session;

    @BeforeEach
    void setUp() {
        cashRegister = mock(CashRegister.class);
        staff = mock(Staff.class);

        session = CashRegisterSession.builder()
                .openingBalance(new Money(10000))
                .currentBalance(new Money(10000))
                .expectedPawnInterest(new Money(500))
                .status(CashRegisterSessionStatus.OPEN)
                .cashRegister(cashRegister)
                .staff(staff)
                .build();
    }

    // ========== CREATE TESTS ==========

    @Test
    @DisplayName("create() should initialize session with correct opening balance")
    void testCreate_InitializesWithOpeningBalance() {
        Money openingBalance = new Money(5000);
        List<Pawn> maturedPawns = List.of();

        CashRegisterSession created = CashRegisterSession.create(openingBalance, cashRegister, staff, maturedPawns);

        assertNotNull(created);
        assertEquals(5000, created.getOpeningBalance().amount());
        assertEquals(5000, created.getCurrentBalance().amount());
        assertEquals(CashRegisterSessionStatus.OPEN, created.getStatus());
        assertEquals(cashRegister, created.getCashRegister());
        assertEquals(staff, created.getStaff());
    }

    @Test
    @DisplayName("create() should calculate expected pawn interest from matured pawns")
    void testCreate_CalculatesExpectedPawnInterest() {
        Pawn pawn1 = createMockPawn(1000);
        Pawn pawn2 = createMockPawn(500);
        Pawn pawn3 = createMockPawn(300);
        List<Pawn> maturedPawns = List.of(pawn1, pawn2, pawn3);

        CashRegisterSession created = CashRegisterSession.create(new Money(10000), cashRegister, staff, maturedPawns);

        assertEquals(1800, created.getExpectedPawnInterest().amount()); // 1000 + 500 + 300
    }

    @Test
    @DisplayName("create() with no matured pawns should have zero expected interest")
    void testCreate_NoMaturedPawns() {
        List<Pawn> maturedPawns = List.of();

        CashRegisterSession created = CashRegisterSession.create(new Money(10000), cashRegister, staff, maturedPawns);

        assertEquals(0, created.getExpectedPawnInterest().amount());
    }

    @Test
    @DisplayName("create() should add OPEN_SESSION transaction")
    void testCreate_AddsOpenSessionTransaction() {
        CashRegisterSession created = CashRegisterSession.create(new Money(10000), cashRegister, staff, List.of());

        assertEquals(1, created.getTransactions().size());
        CashRegisterTransaction tx = (CashRegisterTransaction) created.getTransactions().get(0);
        assertEquals(CashRegisterTransactionAction.OPEN_SESSION, tx.getAction());
        assertEquals(TransactionDirection.NEUTRAL, tx.getDirection());
        assertEquals(0, tx.getAmount().amount());
        assertEquals(staff, tx.getStaff());
    }

    @Test
    @DisplayName("create() with zero opening balance should be allowed")
    void testCreate_ZeroOpeningBalance() {
        CashRegisterSession created = CashRegisterSession.create(new Money(0), cashRegister, staff, List.of());

        assertEquals(0, created.getOpeningBalance().amount());
        assertEquals(0, created.getCurrentBalance().amount());
    }

    // ========== CLOSE TESTS ==========

    @Test
    @DisplayName("close() with exact balance should have NONE discrepancy")
    void testClose_ExactBalance() {
        Money closingBalance = new Money(10000); // Matches currentBalance

        CashRegisterSessionCloseResult result = session.close(closingBalance, staff);

        assertEquals(CashRegisterSessionStatus.CLOSED, session.getStatus());
        assertEquals(10000, session.getClosingBalance().amount());
        assertEquals(0, session.getDiscrepancy().amount());
        assertEquals(CashRegisterSessionDiscrepancyType.NONE, session.getDiscrepancyType());
        assertNotNull(session.getClosedAt());
        assertEquals(0, result.discrepancy().amount());
        assertEquals(CashRegisterSessionDiscrepancyType.NONE, result.discrepancyType());
    }

    @Test
    @DisplayName("close() with overage should calculate positive discrepancy")
    void testClose_Overage() {
        Money closingBalance = new Money(12000); // 2000 more than currentBalance

        CashRegisterSessionCloseResult result = session.close(closingBalance, staff);

        assertEquals(CashRegisterSessionStatus.CLOSED, session.getStatus());
        assertEquals(12000, session.getClosingBalance().amount());
        assertEquals(2000, session.getDiscrepancy().amount());
        assertEquals(CashRegisterSessionDiscrepancyType.OVERAGE, session.getDiscrepancyType());
        assertEquals(2000, result.discrepancy().amount());
        assertEquals(CashRegisterSessionDiscrepancyType.OVERAGE, result.discrepancyType());
    }

    @Test
    @DisplayName("close() with shortage should calculate negative discrepancy")
    void testClose_Shortage() {
        Money closingBalance = new Money(8000); // 2000 less than currentBalance

        CashRegisterSessionCloseResult result = session.close(closingBalance, staff);

        assertEquals(CashRegisterSessionStatus.CLOSED, session.getStatus());
        assertEquals(8000, session.getClosingBalance().amount());
        assertEquals(2000, session.getDiscrepancy().amount()); // absoluteSubtract
        assertEquals(CashRegisterSessionDiscrepancyType.SHORTAGE, session.getDiscrepancyType());
        assertEquals(2000, result.discrepancy().amount());
        assertEquals(CashRegisterSessionDiscrepancyType.SHORTAGE, result.discrepancyType());
    }

    @ParameterizedTest
    @CsvSource({
            "10000, 10000, 0, NONE",      // Exact match
            "10000, 11000, 1000, OVERAGE", // Overage
            "10000, 9000, 1000, SHORTAGE", // Shortage
            "10000, 10001, 1, OVERAGE",    // Small overage
            "10000, 9999, 1, SHORTAGE"     // Small shortage
    })
    @DisplayName("close() should calculate discrepancy correctly")
    void testClose_DiscrepancyCalculation(int currentBalance, int closingBalance, int expectedDiscrepancy, CashRegisterSessionDiscrepancyType expectedType) {
        session = CashRegisterSession.builder()
                .openingBalance(new Money(10000))
                .currentBalance(new Money(currentBalance))
                .expectedPawnInterest(new Money(0))
                .status(CashRegisterSessionStatus.OPEN)
                .cashRegister(cashRegister)
                .staff(staff)
                .build();

        CashRegisterSessionCloseResult result = session.close(new Money(closingBalance), staff);

        assertEquals(expectedDiscrepancy, session.getDiscrepancy().amount());
        assertEquals(expectedType, session.getDiscrepancyType());
    }

    @Test
    @DisplayName("close() should add CLOSE_SESSION transaction")
    void testClose_AddsCloseSessionTransaction() {
        session.close(new Money(10000), staff);

        assertEquals(1, session.getTransactions().size());
        CashRegisterTransaction tx = (CashRegisterTransaction) session.getTransactions().get(0);
        assertEquals(CashRegisterTransactionAction.CLOSE_SESSION, tx.getAction());
        assertEquals(TransactionDirection.NEUTRAL, tx.getDirection());
        assertEquals(0, tx.getAmount().amount());
    }

    @Test
    @DisplayName("close() on already closed session should throw IllegalStateException")
    void testClose_AlreadyClosed() {
        session.close(new Money(10000), staff);

        assertThrows(IllegalStateException.class, () -> session.close(new Money(10000), staff));
    }

    @Test
    @DisplayName("close() should set closedAt timestamp")
    void testClose_SetsClosedAt() {
        assertNull(session.getClosedAt());

        session.close(new Money(10000), staff);

        assertNotNull(session.getClosedAt());
    }

    // ========== WITHDRAW TESTS ==========

    @Test
    @DisplayName("withdraw() should decrease current balance")
    void testWithdraw_DecreasesBalance() {
        session.withdraw(new Money(2000), "Cash withdrawal");

        assertEquals(8000, session.getCurrentBalance().amount()); // 10000 - 2000
    }

    @Test
    @DisplayName("withdraw() should add WITHDRAW transaction")
    void testWithdraw_AddsTransaction() {
        session.withdraw(new Money(2000), "Withdrawal for supplies");

        assertEquals(1, session.getTransactions().size());
        CashRegisterTransaction tx = (CashRegisterTransaction) session.getTransactions().get(0);
        assertEquals(CashRegisterTransactionAction.WITHDRAW, tx.getAction());
        assertEquals(TransactionDirection.OUT, tx.getDirection());
        assertEquals(2000, tx.getAmount().amount());
        assertEquals("Withdrawal for supplies", tx.getDescription());
    }

    @Test
    @DisplayName("withdraw() multiple times should accumulate")
    void testWithdraw_MultipleWithdrawals() {
        session.withdraw(new Money(1000), "First");
        session.withdraw(new Money(500), "Second");
        session.withdraw(new Money(300), "Third");

        assertEquals(8200, session.getCurrentBalance().amount()); // 10000 - 1000 - 500 - 300
        assertEquals(3, session.getTransactions().size());
    }

    @Test
    @DisplayName("withdraw() on closed session should throw IllegalStateException")
    void testWithdraw_OnClosedSession() {
        session.close(new Money(10000), staff);

        assertThrows(IllegalStateException.class, () -> session.withdraw(new Money(1000), "Withdrawal"));
    }

    @Test
    @DisplayName("withdraw() entire balance should leave zero")
    void testWithdraw_EntireBalance() {
        session.withdraw(new Money(10000), "Withdraw all");

        assertEquals(0, session.getCurrentBalance().amount());
    }

    // ========== DEPOSIT TESTS ==========

    @Test
    @DisplayName("deposit() should increase current balance")
    void testDeposit_IncreasesBalance() {
        session.deposit(new Money(3000), "Cash deposit");

        assertEquals(13000, session.getCurrentBalance().amount()); // 10000 + 3000
    }

    @Test
    @DisplayName("deposit() should add DEPOSIT transaction")
    void testDeposit_AddsTransaction() {
        session.deposit(new Money(3000), "Deposit from sale");

        assertEquals(1, session.getTransactions().size());
        CashRegisterTransaction tx = (CashRegisterTransaction) session.getTransactions().get(0);
        assertEquals(CashRegisterTransactionAction.DEPOSIT, tx.getAction());
        assertEquals(TransactionDirection.IN, tx.getDirection());
        assertEquals(3000, tx.getAmount().amount());
        assertEquals("Deposit from sale", tx.getDescription());
    }

    @Test
    @DisplayName("deposit() multiple times should accumulate")
    void testDeposit_MultipleDeposits() {
        session.deposit(new Money(1000), "First");
        session.deposit(new Money(500), "Second");
        session.deposit(new Money(300), "Third");

        assertEquals(11800, session.getCurrentBalance().amount()); // 10000 + 1000 + 500 + 300
        assertEquals(3, session.getTransactions().size());
    }

    @Test
    @DisplayName("deposit() on closed session should throw IllegalStateException")
    void testDeposit_OnClosedSession() {
        session.close(new Money(10000), staff);

        assertThrows(IllegalStateException.class, () -> session.deposit(new Money(1000), "Deposit"));
    }

    @Test
    @DisplayName("deposit() zero amount should be allowed")
    void testDeposit_ZeroAmount() {
        session.deposit(new Money(0), "Zero deposit");

        assertEquals(10000, session.getCurrentBalance().amount());
        assertEquals(1, session.getTransactions().size());
    }

    // ========== RECORD TRANSACTION TESTS ==========

    @Test
    @DisplayName("recordTransaction() with IN direction should increase balance")
    void testRecordTransaction_InDirection() {
        Transaction transaction = mock(Transaction.class);
        when(transaction.getAmount()).thenReturn(new Money(2000));
        when(transaction.getDirection()).thenReturn(TransactionDirection.IN);

        session.recordTransaction(transaction);

        assertEquals(12000, session.getCurrentBalance().amount()); // 10000 + 2000
    }

    @Test
    @DisplayName("recordTransaction() with OUT direction should decrease balance")
    void testRecordTransaction_OutDirection() {
        Transaction transaction = mock(Transaction.class);
        when(transaction.getAmount()).thenReturn(new Money(3000));
        when(transaction.getDirection()).thenReturn(TransactionDirection.OUT);

        session.recordTransaction(transaction);

        assertEquals(7000, session.getCurrentBalance().amount()); // 10000 - 3000
    }

    @Test
    @DisplayName("recordTransaction() with NEUTRAL direction should not affect balance")
    void testRecordTransaction_NeutralDirection() {
        Transaction transaction = mock(Transaction.class);
        when(transaction.getAmount()).thenReturn(new Money(0));
        when(transaction.getDirection()).thenReturn(TransactionDirection.NEUTRAL);

        session.recordTransaction(transaction);

        assertEquals(10000, session.getCurrentBalance().amount()); // Unchanged
    }

    @Test
    @DisplayName("recordTransaction() should not add to transactions list")
    void testRecordTransaction_DoesNotAddToList() {
        Transaction transaction = mock(Transaction.class);
        when(transaction.getAmount()).thenReturn(new Money(2000));
        when(transaction.getDirection()).thenReturn(TransactionDirection.IN);

        session.recordTransaction(transaction);

        assertEquals(0, session.getTransactions().size()); // Not added to list
    }

    @Test
    @DisplayName("recordTransaction() on closed session should throw IllegalStateException")
    void testRecordTransaction_OnClosedSession() {
        session.close(new Money(10000), staff);

        Transaction transaction = mock(Transaction.class);
        when(transaction.getAmount()).thenReturn(new Money(1000));
        when(transaction.getDirection()).thenReturn(TransactionDirection.IN);

        assertThrows(IllegalStateException.class, () -> session.recordTransaction(transaction));
    }

    @Test
    @DisplayName("recordTransaction() multiple times should accumulate balance changes")
    void testRecordTransaction_MultipleTransactions() {
        Transaction tx1 = mock(Transaction.class);
        when(tx1.getAmount()).thenReturn(new Money(1000));
        when(tx1.getDirection()).thenReturn(TransactionDirection.IN);

        Transaction tx2 = mock(Transaction.class);
        when(tx2.getAmount()).thenReturn(new Money(500));
        when(tx2.getDirection()).thenReturn(TransactionDirection.OUT);

        Transaction tx3 = mock(Transaction.class);
        when(tx3.getAmount()).thenReturn(new Money(300));
        when(tx3.getDirection()).thenReturn(TransactionDirection.IN);

        session.recordTransaction(tx1);
        session.recordTransaction(tx2);
        session.recordTransaction(tx3);

        assertEquals(10800, session.getCurrentBalance().amount()); // 10000 + 1000 - 500 + 300
    }

    // ========== INTEGRATION/WORKFLOW TESTS ==========

    @Test
    @DisplayName("Full session lifecycle: create -> deposit -> withdraw -> close")
    void testFullSessionLifecycle() {
        // Create
        CashRegisterSession created = CashRegisterSession.create(new Money(5000), cashRegister, staff, List.of());

        // Deposit
        created.deposit(new Money(3000), "Morning deposit");
        assertEquals(8000, created.getCurrentBalance().amount());

        // Withdraw
        created.withdraw(new Money(1000), "Supplies");
        assertEquals(7000, created.getCurrentBalance().amount());

        // Close
        CashRegisterSessionCloseResult result = created.close(new Money(7200), staff);

        assertEquals(CashRegisterSessionStatus.CLOSED, created.getStatus());
        assertEquals(200, result.discrepancy().amount());
        assertEquals(CashRegisterSessionDiscrepancyType.OVERAGE, result.discrepancyType());
    }

    @Test
    @DisplayName("Session with transactions: create -> recordTransaction multiple times -> close")
    void testSessionWithRecordedTransactions() {
        CashRegisterSession created = CashRegisterSession.create(new Money(10000), cashRegister, staff, List.of());

        // Record pawn creation (OUT)
        Transaction pawnTx = mock(Transaction.class);
        when(pawnTx.getAmount()).thenReturn(new Money(5000));
        when(pawnTx.getDirection()).thenReturn(TransactionDirection.OUT);
        created.recordTransaction(pawnTx);

        // Record pawn redemption (IN)
        Transaction redeemTx = mock(Transaction.class);
        when(redeemTx.getAmount()).thenReturn(new Money(5500));
        when(redeemTx.getDirection()).thenReturn(TransactionDirection.IN);
        created.recordTransaction(redeemTx);

        assertEquals(10500, created.getCurrentBalance().amount()); // 10000 - 5000 + 5500

        CashRegisterSessionCloseResult result = created.close(new Money(10500), staff);
        assertEquals(CashRegisterSessionDiscrepancyType.NONE, result.discrepancyType());
    }

    @Test
    @DisplayName("Mixed operations: deposit, withdraw, recordTransaction")
    void testMixedOperations() {
        session.deposit(new Money(2000), "Deposit");
        assertEquals(12000, session.getCurrentBalance().amount());

        Transaction tx = mock(Transaction.class);
        when(tx.getAmount()).thenReturn(new Money(3000));
        when(tx.getDirection()).thenReturn(TransactionDirection.OUT);
        session.recordTransaction(tx);
        assertEquals(9000, session.getCurrentBalance().amount());

        session.withdraw(new Money(1000), "Withdraw");
        assertEquals(8000, session.getCurrentBalance().amount());

        CashRegisterSessionCloseResult result = session.close(new Money(8000), staff);
        assertEquals(CashRegisterSessionDiscrepancyType.NONE, result.discrepancyType());
    }

    @Test
    @DisplayName("Session with large discrepancy should calculate correctly")
    void testLargeDiscrepancy() {
        session.deposit(new Money(50000), "Large deposit");
        assertEquals(60000, session.getCurrentBalance().amount());

        CashRegisterSessionCloseResult result = session.close(new Money(55000), staff);

        assertEquals(5000, result.discrepancy().amount());
        assertEquals(CashRegisterSessionDiscrepancyType.SHORTAGE, result.discrepancyType());
    }

    // ========== HELPER METHODS ==========

    private Pawn createMockPawn(int interestAmount) {
        Pawn pawn = mock(Pawn.class);
        when(pawn.getInterest()).thenReturn(new Money(interestAmount));
        return pawn;
    }
}