package com.volter.shop.modules.expense.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.expense.application.dto.ExpenseCategoryBucket;
import com.volter.shop.modules.expense.application.dto.ExpenseCreateRequest;
import com.volter.shop.modules.expense.application.dto.ExpenseFilterRequest;
import com.volter.shop.modules.expense.application.dto.ExpenseMonthlySummary;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;
import com.volter.shop.modules.expense.domain.model.ExpenseTransaction;
import com.volter.shop.modules.expense.domain.repository.ExpenseRepository;
import com.volter.shop.modules.expense.domain.repository.ExpenseTransactionRepository;
import com.volter.shop.modules.expense.domain.specification.ExpenseSpecification;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseTransactionRepository expenseTxRepository;
    private final CashRegisterService cashRegisterService;
    private final TransactionService transactionService;
    private final StaffService staffService;

    /**
     * Expenses the caller is allowed to see: their own plus those of every staff
     * member below them in the management tree (recursive), narrowed by the filter.
     */
    @Transactional(readOnly = true)
    public Page<Expense> list(ExpenseFilterRequest filter, Pageable pageable, Long staffId) {
        List<Long> visibleStaffIds = new ArrayList<>(staffService.findSubordinateStaffIds(staffId));
        visibleStaffIds.add(staffId);
        return expenseRepository.findAll(
                ExpenseSpecification.matches(filter).and(ExpenseSpecification.staffIdIn(visibleStaffIds)),
                pageable);
    }

    /**
     * Expenses the caller may see, grouped by calendar month: each month carries
     * the grand total/count plus a per-category breakdown. Narrowed by the same
     * filter as the list (category + date range).
     */
    @Transactional(readOnly = true)
    public List<ExpenseMonthlySummary> summarizeByMonth(ExpenseFilterRequest filter, Long staffId) {
        List<Long> visibleStaffIds = new ArrayList<>(staffService.findSubordinateStaffIds(staffId));
        visibleStaffIds.add(staffId);

        // Optionally narrow to one staff member, but only within the caller's team.
        List<Long> scope = filter.staffId() == null ? visibleStaffIds
                : (visibleStaffIds.contains(filter.staffId()) ? List.of(filter.staffId()) : List.of());
        if (scope.isEmpty()) {
            return List.of();
        }

        LocalDate from = filter.dateFrom() != null ? filter.dateFrom() : LocalDate.of(1, 1, 1);
        LocalDate to = filter.dateTo() != null ? filter.dateTo() : LocalDate.of(9999, 12, 31);

        List<ExpenseCategoryBucket> buckets = expenseRepository.summarizeByMonthAndCategory(
                scope, filter.category(), from, to);

        // Collapse the (year, month, category) buckets into one summary per month.
        Map<List<Integer>, List<ExpenseCategoryBucket>> byMonth = buckets.stream()
                .collect(Collectors.groupingBy(b -> List.of(b.year(), b.month()), LinkedHashMap::new, Collectors.toList()));

        List<ExpenseMonthlySummary> summaries = new ArrayList<>();
        for (var entry : byMonth.entrySet()) {
            List<ExpenseCategoryBucket> monthBuckets = entry.getValue();
            long total = monthBuckets.stream().mapToLong(ExpenseCategoryBucket::total).sum();
            long count = monthBuckets.stream().mapToLong(ExpenseCategoryBucket::count).sum();
            Map<ExpenseCategory, Long> byCategory = monthBuckets.stream()
                    .collect(Collectors.toMap(ExpenseCategoryBucket::category, ExpenseCategoryBucket::total));
            summaries.add(new ExpenseMonthlySummary(entry.getKey().get(0), entry.getKey().get(1), total, count, byCategory));
        }

        summaries.sort(Comparator.comparingInt(ExpenseMonthlySummary::year)
                .thenComparingInt(ExpenseMonthlySummary::month).reversed());
        return summaries;
    }

    /**
     * A single expense, only if it is the caller's own or was recorded by someone they manage.
     */
    @Transactional(readOnly = true)
    public Expense get(Long id, Long staffId) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        if (!Objects.equals(expense.getStaffId(), staffId) && !staffService.isManagerOf(staffId, expense.getStaffId())) {
            throw new AccessDeniedException("Cannot access an expense recorded by another staff member");
        }
        return expense;
    }

    /**
     * Record a new expense.
     * Record general transaction and expense transaction.
     * Record money flow from cash register session.
     */
    public Expense record(ExpenseCreateRequest request, Long staffId) {
        CashRegisterSession session = cashRegisterService.requireOpenSession(request.cashRegisterSessionId());
        Money amount = new Money(request.amount());

        Expense expense = expenseRepository.save(Expense.create(
                staffId, request.category(), amount, request.description(), request.date()));

        Transaction tx = transactionService.record(
                staffId, session, TransactionType.EXPENSE, amount,
                TransactionDirection.OUT, request.description());
        expenseTxRepository.save(ExpenseTransaction.record(tx, expense));

        cashRegisterService.applyTransaction(tx);

        return expense;
    }
}
