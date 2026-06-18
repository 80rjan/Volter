package com.volter.shop.modules.expense.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.expense.application.dto.ExpenseCreateRequest;
import com.volter.shop.modules.expense.application.dto.ExpenseFilterRequest;
import com.volter.shop.modules.expense.domain.model.Expense;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
