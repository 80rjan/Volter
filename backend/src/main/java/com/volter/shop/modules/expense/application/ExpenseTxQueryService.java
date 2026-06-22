package com.volter.shop.modules.expense.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.expense.application.dto.ExpenseResponse;
import com.volter.shop.modules.expense.domain.repository.ExpenseRepository;
import com.volter.shop.modules.expense.domain.repository.ExpenseTransactionRepository;
import com.volter.shop.modules.expense.infrastructure.mapper.ExpenseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Read-only expense lookup by transaction id, consumed by the transaction module
 * when assembling a transaction's detailed view. Kept separate from
 * {@link ExpenseService} to avoid a service dependency cycle: it touches only
 * expense repositories/mappers (plus staff-name resolution).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseTxQueryService {

    private final ExpenseTransactionRepository expenseTxRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final StaffService staffService;

    /** The expense behind an EXPENSE transaction, if any. */
    public Optional<ExpenseResponse> findDetailByTransactionId(Long transactionId) {
        return expenseTxRepository.findExpenseIdByTransactionId(transactionId)
                .flatMap(expenseRepository::findById)
                .map(expense -> expenseMapper.toResponse(
                        expense,
                        staffService.findStaffNames(Set.of(expense.getStaffId())).get(expense.getStaffId())));
    }
}
