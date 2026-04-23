package com.volter.shop.modules.expense.application;


import com.volter.shop.modules.expense.application.dto.filter.ExpenseFilter;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.domain.repository.ExpenseRepository;
import com.volter.shop.modules.expense.domain.specification.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Transactional
    public Page<Expense> getAll(ExpenseFilter filters, Pageable pageable) {
        Specification<Expense> spec = ExpenseSpecification.withFilters(filters);
        return expenseRepository.findAll(spec, pageable);
    }

    @Transactional
    public List<Expense> getAllByMonthAndYear(Integer month, Integer year) {
        return expenseRepository.findByMonthAndYear(month, year);
    }

//    @Transactional
//    public ExpenseSummaryDTO getExpenseSummary(Integer month, Integer year) {
//        List<Expense> expenses = getAllByMonthAndYear(month, year);
//
//        Map<ExpenseType, Integer> totalByType = expenses.stream()
//                .collect(Collectors.toMap(
//                        Expense::getExpenseType,
//                        Expense::getAmount,
//                        Integer::sum
//                ));
//
//        Integer grandTotal = expenses.stream().map(Expense::getAmount).reduce(0, Integer::sum);
//
//        return new ExpenseSummaryDTO(month, year, totalByType, grandTotal);
//    }
//
//    @Transactional
//    public Expense create(ExpenseCreationRequest request, String transactionDescription, Long cashRegisterId, Authentication authentication) {
//        Staff staff = staffService.getById(validate.extractStaffId(authentication));
//        CashRegister cashRegister = cashRegisterService.getById(cashRegisterId);
//
//        Expense expense = expenseMapper.toEntity(request);
//
//        expense.setStaff(staff);
//
//        ExpenseTransaction transaction = ExpenseTransaction.builder()
//                .action(TransactionAction.CREATION)
//                .cashIn(0)
//                .cashOut(expense.getAmount())
//                .profit(0)
//                .description(transactionDescription)
//                .expense(expense)
//                .staff(staff)
//                .cashRegister(cashRegister)
//                .build();
//
//        cashRegister.setBalance(cashRegister.getBalance() - expense.getAmount());
//
//        expenseRepository.save(expense);
//        transactionService.save(transaction);
//        cashRegisterService.save(cashRegister);
//
//        return expense;
//    }
}
