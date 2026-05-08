package com.volter.shop.expense;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.expense.application.ExpenseService;
import com.volter.shop.modules.expense.web.request.ExpenseFilterRequest;
import com.volter.shop.modules.expense.web.request.ExpenseCreationRequest;
import com.volter.shop.modules.expense.web.response.ExpenseSummaryResponse;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;
import com.volter.shop.modules.expense.infrastructure.ExpenseRepository;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private StaffService staffService;
    @Mock
    private CashRegisterService cashRegisterService;

    @InjectMocks
    private ExpenseService expenseService;

    private Staff staff;
    private CashRegisterSession session;

    @BeforeEach
    void setUp() {
        staff = mock(Staff.class);
        lenient().when(staff.getId()).thenReturn(1L);

        session = mock(CashRegisterSession.class);
    }

    // =========================================================================
    // getAll()
    // =========================================================================

    @Nested
    @DisplayName("getAll()")
    class GetAll {

        @Test
        @DisplayName("returns page from repository")
        void returnsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Expense> page = new PageImpl<>(List.of(mock(Expense.class)), pageable, 1);

            when(expenseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Expense> result = expenseService.getAll(new ExpenseFilterRequest(), pageable);

            assertEquals(1, result.getTotalElements());
            verify(expenseRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("returns empty page when no expenses match the filter")
        void emptyFilter_returnsEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);

            when(expenseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

            assertTrue(expenseService.getAll(new ExpenseFilterRequest(), pageable).isEmpty());
        }

        @Test
        @DisplayName("expenseType filter is forwarded to the specification")
        void typeFilter_forwardedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            ExpenseFilterRequest filter = new ExpenseFilterRequest();
            filter.setExpenseType(ExpenseType.RENT);

            when(expenseRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(new PageImpl<>(List.of(mock(Expense.class))));

            assertEquals(1, expenseService.getAll(filter, pageable).getTotalElements());
            verify(expenseRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("date range filter is forwarded to the specification")
        void dateRangeFilter_forwardedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            ExpenseFilterRequest filter = new ExpenseFilterRequest();
            filter.setFromDate(LocalDate.of(2025, 1, 1));
            filter.setToDate(LocalDate.of(2025, 1, 31));

            when(expenseRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(new PageImpl<>(List.of(mock(Expense.class))));

            expenseService.getAll(filter, pageable);

            verify(expenseRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("second page returns correct pagination metadata")
        void pagination_secondPage() {
            Pageable pageable = PageRequest.of(1, 5);
            Page<Expense> page = new PageImpl<>(List.of(mock(Expense.class)), pageable, 10);

            when(expenseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Expense> result = expenseService.getAll(new ExpenseFilterRequest(), pageable);

            assertEquals(1, result.getNumber());
            assertEquals(10, result.getTotalElements());
            assertEquals(2, result.getTotalPages());
        }

        @Test
        @DisplayName("sorted pageable is forwarded to repository")
        void sortedPageable_forwardedToRepository() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());

            when(expenseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

            expenseService.getAll(new ExpenseFilterRequest(), pageable);

            verify(expenseRepository).findAll(any(Specification.class), eq(pageable));
        }
    }

    // =========================================================================
    // getAllGrouped()
    // =========================================================================

    @Nested
    @DisplayName("getAllGrouped()")
    class GetAllGrouped {

        private Expense expense(ExpenseType type, int amount) {
            return Expense.builder()
                    .expenseType(type)
                    .amount(new Money(amount))
                    .description("test")
                    .date(LocalDate.of(2025, 5, 1))
                    .build();
        }

        @Test
        @DisplayName("returns correct grand total for a single expense")
        void singleExpense_correctGrandTotal() {
            when(expenseRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(expense(ExpenseType.RENT, 1000)));

            ExpenseSummaryResponse r = expenseService.getAllGrouped(new ExpenseFilterRequest());

            assertEquals(new Money(1000), r.grandTotal());
        }

        @Test
        @DisplayName("sums amounts for the same expense type")
        void sameType_sumsAmounts() {
            when(expenseRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(
                            expense(ExpenseType.RENT, 1000),
                            expense(ExpenseType.RENT, 500)
                    ));

            ExpenseSummaryResponse r = expenseService.getAllGrouped(new ExpenseFilterRequest());

            assertEquals(new Money(1500), r.totalByType().get(ExpenseType.RENT));
            assertEquals(new Money(1500), r.grandTotal());
        }

        @Test
        @DisplayName("groups different expense types into separate map entries")
        void differentTypes_groupedSeparately() {
            when(expenseRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(
                            expense(ExpenseType.RENT, 1000),
                            expense(ExpenseType.UTILITIES, 200)
                    ));

            ExpenseSummaryResponse r = expenseService.getAllGrouped(new ExpenseFilterRequest());

            assertEquals(new Money(1000), r.totalByType().get(ExpenseType.RENT));
            assertEquals(new Money(200), r.totalByType().get(ExpenseType.UTILITIES));
            assertEquals(new Money(1200), r.grandTotal());
        }

        @Test
        @DisplayName("empty expense list returns zero grand total")
        void emptyList_returnsZeroGrandTotal() {
            when(expenseRepository.findAll(any(Specification.class))).thenReturn(List.of());

            ExpenseSummaryResponse r = expenseService.getAllGrouped(new ExpenseFilterRequest());

            assertEquals(new Money(0), r.grandTotal());
            assertTrue(r.totalByType().isEmpty());
        }

        @Test
        @DisplayName("fromDate and toDate from the filter are included in the response")
        void filterDates_includedInResponse() {
            ExpenseFilterRequest filter = new ExpenseFilterRequest();
            filter.setFromDate(LocalDate.of(2025, 1, 1));
            filter.setToDate(LocalDate.of(2025, 1, 31));

            when(expenseRepository.findAll(any(Specification.class))).thenReturn(List.of());

            ExpenseSummaryResponse r = expenseService.getAllGrouped(filter);

            assertEquals(LocalDate.of(2025, 1, 1), r.fromDate());
            assertEquals(LocalDate.of(2025, 1, 31), r.toDate());
        }

        @Test
        @DisplayName("grand total equals sum of all per-type totals")
        void grandTotal_equalsSumOfAllPerTypeTotals() {
            when(expenseRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(
                            expense(ExpenseType.RENT, 1000),
                            expense(ExpenseType.UTILITIES, 200),
                            expense(ExpenseType.SALARIES, 5000)
                    ));

            ExpenseSummaryResponse r = expenseService.getAllGrouped(new ExpenseFilterRequest());

            int sumOfTypes = r.totalByType().values().stream().mapToInt(Money::amount).sum();
            assertEquals(r.grandTotal().amount(), sumOfTypes);
        }
    }

    // =========================================================================
    // create()
    // =========================================================================

    @Nested
    @DisplayName("create()")
    class Create {

        private ExpenseCreationRequest request() {
            return ExpenseCreationRequest.builder()
                    .expenseType(ExpenseType.RENT)
                    .amount(1500)
                    .description("Monthly rent")
                    .date(LocalDate.of(2025, 5, 1))
                    .transactionDescription("Rent payment")
                    .build();
        }

        @BeforeEach
        void stubCollaborators() {
            when(staffService.getCurrentStaff()).thenReturn(staff);
            when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(session);
            when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));
            when(cashRegisterService.saveSession(session)).thenReturn(session);
        }

        @Test
        @DisplayName("resolves current staff from security context")
        void resolvesCurrentStaff() {
            expenseService.create(request());

            verify(staffService).getCurrentStaff();
        }

        @Test
        @DisplayName("fetches the open cash register session for the current staff")
        void fetchesOpenSession_forCurrentStaff() {
            expenseService.create(request());

            verify(cashRegisterService).getOpenSessionByStaff(1L);
        }

        @Test
        @DisplayName("persists the expense via repository")
        void persistsExpense() {
            expenseService.create(request());

            verify(expenseRepository).save(any(Expense.class));
        }

        @Test
        @DisplayName("records the initial transaction on the cash register session")
        void recordsTransactionOnSession() {
            expenseService.create(request());

            verify(session).recordTransaction(any());
        }

        @Test
        @DisplayName("saves the updated cash register session")
        void savesUpdatedSession() {
            expenseService.create(request());

            verify(cashRegisterService).saveSession(session);
        }

        @Test
        @DisplayName("returns the created expense")
        void returnsCreatedExpense() {
            Expense result = expenseService.create(request());

            assertNotNull(result);
        }

        @Test
        @DisplayName("created expense has the type and amount from the request")
        void createdExpense_hasCorrectTypeAndAmount() {
            ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);

            expenseService.create(request());

            verify(expenseRepository).save(captor.capture());
            Expense saved = captor.getValue();
            assertEquals(ExpenseType.RENT, saved.getExpenseType());
            assertEquals(new Money(1500), saved.getAmount());
        }

        @Test
        @DisplayName("created expense has the description from the request")
        void createdExpense_hasCorrectDescription() {
            ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);

            expenseService.create(request());

            verify(expenseRepository).save(captor.capture());
            assertEquals("Monthly rent", captor.getValue().getDescription());
        }

        @Test
        @DisplayName("operations happen in the correct order: save expense, record transaction, save session")
        void operationsInOrder() {
            var order = inOrder(expenseRepository, session, cashRegisterService);

            expenseService.create(request());

            order.verify(expenseRepository).save(any(Expense.class));
            order.verify(session).recordTransaction(any());
            order.verify(cashRegisterService).saveSession(session);
        }
    }
}
