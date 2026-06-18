package com.volter.shop.expense;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.expense.application.ExpenseService;
import com.volter.shop.modules.expense.application.dto.ExpenseCreateRequest;
import com.volter.shop.modules.expense.application.dto.ExpenseFilterRequest;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.domain.model.ExpenseTransaction;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;
import com.volter.shop.modules.expense.domain.repository.ExpenseRepository;
import com.volter.shop.modules.expense.domain.repository.ExpenseTransactionRepository;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private ExpenseTransactionRepository expenseTxRepository;
    @Mock
    private CashRegisterService cashRegisterService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private StaffService staffService;

    @InjectMocks
    private ExpenseService expenseService;

    @Nested
    @DisplayName("list() / get()")
    class Read {

        @Test
        @DisplayName("list scopes to the caller's team and forwards to the repository")
        void list_scopesToTeam() {
            Pageable pageable = PageRequest.of(0, 10);
            when(staffService.findSubordinateStaffIds(3L)).thenReturn(java.util.List.of(4L, 5L));
            when(expenseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

            expenseService.list(new ExpenseFilterRequest(ExpenseCategory.RENT, null, null, null), pageable, 3L);

            verify(staffService).findSubordinateStaffIds(3L);
            verify(expenseRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("get returns the caller's own expense")
        void get_own() {
            Expense expense = mock(Expense.class);
            when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
            when(expense.getStaffId()).thenReturn(3L);

            assertSame(expense, expenseService.get(1L, 3L));
            verifyNoInteractions(staffService);
        }

        @Test
        @DisplayName("get returns a subordinate's expense for their manager")
        void get_subordinate() {
            Expense expense = mock(Expense.class);
            when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
            when(expense.getStaffId()).thenReturn(5L);
            when(staffService.isManagerOf(3L, 5L)).thenReturn(true);

            assertSame(expense, expenseService.get(1L, 3L));
        }

        @Test
        @DisplayName("get forbids an expense recorded by an unrelated staff member")
        void get_unrelated_throws() {
            Expense expense = mock(Expense.class);
            when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
            when(expense.getStaffId()).thenReturn(5L);
            when(staffService.isManagerOf(3L, 5L)).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> expenseService.get(1L, 3L));
        }

        @Test
        @DisplayName("get throws when the expense is missing")
        void get_missing() {
            when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> expenseService.get(1L, 3L));
        }
    }

    @Nested
    @DisplayName("record()")
    class Record {

        private final ExpenseCreateRequest request = new ExpenseCreateRequest(
                ExpenseCategory.RENT, 1500, "Monthly rent", LocalDate.of(2025, 5, 1), 77L);

        private CashRegisterSession session;
        private Transaction tx;

        private void stubHappyPath() {
            session = mock(CashRegisterSession.class);
            tx = mock(Transaction.class);
            when(cashRegisterService.requireOpenSession(77L)).thenReturn(session);
            when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));
            when(transactionService.record(eq(11L), eq(session), eq(TransactionType.EXPENSE),
                    eq(new Money(1500)), eq(TransactionDirection.OUT), eq("Monthly rent"))).thenReturn(tx);
        }

        @Test
        @DisplayName("requires the referenced session to be open")
        void requiresOpenSession() {
            stubHappyPath();

            expenseService.record(request, 11L);

            verify(cashRegisterService).requireOpenSession(77L);
        }

        @Test
        @DisplayName("persists the expense with the request's category, amount and date")
        void persistsExpense() {
            stubHappyPath();

            expenseService.record(request, 11L);

            ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
            verify(expenseRepository).save(captor.capture());
            Expense saved = captor.getValue();
            assertEquals(ExpenseCategory.RENT, saved.getCategory());
            assertEquals(new Money(1500), saved.getAmount());
            assertEquals(LocalDate.of(2025, 5, 1), saved.getDate());
            assertEquals(11L, saved.getStaffId());
        }

        @Test
        @DisplayName("records an outflow transaction and applies it to the session balance")
        void recordsAndAppliesTransaction() {
            stubHappyPath();

            expenseService.record(request, 11L);

            verify(transactionService).record(11L, session, TransactionType.EXPENSE,
                    new Money(1500), TransactionDirection.OUT, "Monthly rent");
            verify(cashRegisterService).applyTransaction(tx);
        }

        @Test
        @DisplayName("links the transaction to the expense via an ExpenseTransaction")
        void linksExpenseTransaction() {
            stubHappyPath();

            expenseService.record(request, 11L);

            verify(expenseTxRepository).save(any(ExpenseTransaction.class));
        }

        @Test
        @DisplayName("saves the expense before applying the cash movement")
        void operationOrder() {
            stubHappyPath();
            var order = inOrder(expenseRepository, transactionService, cashRegisterService, expenseTxRepository);

            expenseService.record(request, 11L);

            order.verify(expenseRepository).save(any(Expense.class));
            order.verify(transactionService).record(anyLong(), any(), any(), any(), any(), any());
            order.verify(expenseTxRepository).save(any(ExpenseTransaction.class));
            order.verify(cashRegisterService).applyTransaction(tx);
        }
    }
}
