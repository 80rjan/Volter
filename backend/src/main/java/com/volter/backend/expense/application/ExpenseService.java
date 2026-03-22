package com.volter.backend.expense.application;

import com.volter.backend.cashRegister.domain.model.CashRegister;
import com.volter.backend.cashRegister.application.CashRegisterService;
import com.volter.backend.expense.domain.repository.ExpenseSpecification;
import com.volter.backend.expense.domain.model.Expense;
import com.volter.backend.expense.domain.repository.ExpenseRepository;
import com.volter.backend.expense.application.dto.ExpenseCreationRequest;
import com.volter.backend.expense.application.dto.ExpenseFilterDTO;
import com.volter.backend.expense.application.dto.ExpenseSummaryDTO;
import com.volter.backend.expense.domain.model.enums.ExpenseType;
import com.volter.backend.expense.infrastructure.ExpenseMapper;
import com.volter.backend.staff.Staff;
import com.volter.backend.staff.StaffService;
import com.volter.backend.transaction.ExpenseTransaction;
import com.volter.backend.transaction.application.TransactionService;
import com.volter.backend.transaction.domain.model.enums.TransactionAction;
import com.volter.backend.common.utils.Validate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final StaffService staffService;
    private final Validate validate;
    private final CashRegisterService cashRegisterService;
    private final ExpenseMapper expenseMapper;
    private final TransactionService transactionService;

    @Transactional
    public Page<Expense> getAll(ExpenseFilterDTO filters, Pageable pageable) {
        Specification<Expense> spec = ExpenseSpecification.withFilters(filters);
        return expenseRepository.findAll(spec, pageable);
    }

    @Transactional
    public List<Expense> getAllByMonthAndYear(Integer month, Integer year) {
        return expenseRepository.findByMonthAndYear(month, year);
    }

    @Transactional
    public ExpenseSummaryDTO getExpenseSummary(Integer month, Integer year) {
        List<Expense> expenses = getAllByMonthAndYear(month, year);

        Map<ExpenseType, Integer> totalByType = expenses.stream()
                .collect(Collectors.toMap(
                        Expense::getExpenseType,
                        Expense::getAmount,
                        Integer::sum
                ));

        Integer grandTotal = expenses.stream().map(Expense::getAmount).reduce(0, Integer::sum);

        return new ExpenseSummaryDTO(month, year, totalByType, grandTotal);
    }

    @Transactional
    public Expense create(ExpenseCreationRequest request, String transactionDescription, Long cashRegisterId, Authentication authentication) {
        Staff staff = staffService.getById(validate.extractStaffId(authentication));
        CashRegister cashRegister = cashRegisterService.getById(cashRegisterId);

        Expense expense = expenseMapper.toEntity(request);

        expense.setStaff(staff);

        ExpenseTransaction transaction = ExpenseTransaction.builder()
                .action(TransactionAction.CREATION)
                .cashIn(0)
                .cashOut(expense.getAmount())
                .profit(0)
                .description(transactionDescription)
                .expense(expense)
                .staff(staff)
                .cashRegister(cashRegister)
                .build();

        cashRegister.setBalance(cashRegister.getBalance() - expense.getAmount());

        expenseRepository.save(expense);
        transactionService.save(transaction);
        cashRegisterService.save(cashRegister);

        return expense;
    }
}
