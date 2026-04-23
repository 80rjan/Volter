package com.volter.shop.modules.expense.api;

import com.volter.shop.modules.expense.application.ExpenseService;
import com.volter.shop.modules.expense.infrastructure.ExpenseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("{api.base.path}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

//    @GetMapping
//    public ResponseEntity<Page<?>> getAll(ExpenseFilter filters, Pageable pageable) {
//        Page<Expense> expenses = expenseService.getAll(filters, pageable);
//
//    }
}
