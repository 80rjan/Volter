package com.volter.shop.modules.expense.application;


import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.expense.web.response.ExpenseSummaryResponse;
import com.volter.shop.modules.expense.web.request.ExpenseFilterRequest;
import com.volter.shop.modules.expense.web.request.ExpenseCreationRequest;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;
import com.volter.shop.modules.expense.infrastructure.ExpenseRepository;
import com.volter.shop.modules.expense.domain.specification.ExpenseSpecification;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole(T(com.volter.identity.domain.model.enums.RoleEnum).MANAGER or " +
              "hasRole(T(com.volter.identity.domain.model.enums.RoleEnum).ADMIN))")
public class ExpenseService {

    // one manager per shop, so every expense added will be added by one and only one manager
    // this can be changed in the future if we want to allow multiple managers per shop, but for now its not needed

    private final ExpenseRepository expenseRepository;
    private final StaffService staffService;
    private final CashRegisterService cashRegisterService;

    @Transactional(readOnly = true)
    public Page<Expense> getAll(ExpenseFilterRequest filters, Pageable pageable) {
        Specification<Expense> spec = ExpenseSpecification.withFilters(filters);
        return expenseRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public ExpenseSummaryResponse getAllGrouped(ExpenseFilterRequest filters) {
        Specification<Expense> spec = ExpenseSpecification.withFilters(filters);
        List<Expense> expenses = expenseRepository.findAll(spec);

        Map<ExpenseType, Money> totalByType = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getExpenseType,
                        Collectors.reducing(new Money(0), Expense::getAmount, Money::add)
                ));

        Money grandTotal = expenses.stream()
                .map(Expense::getAmount)
                .reduce(new Money(0), Money::add);

        return new ExpenseSummaryResponse(filters.getFromDate(), filters.getToDate(), totalByType, grandTotal);
    }

    @Transactional
    public Expense create(ExpenseCreationRequest request) {
        Staff staff = staffService.getCurrentStaff();
        CashRegisterSession cashRegisterSession = cashRegisterService.getOpenSessionByStaff(staff.getId());

        Expense expense = Expense.create(request, staff, cashRegisterSession);
        expenseRepository.save(expense);

        cashRegisterSession.recordTransaction(expense.getInitialTransaction());
        cashRegisterService.saveSession(cashRegisterSession);

        return expense;
    }
}
