package com.volter.shop.modules.expense.web;

import com.volter.shop.modules.expense.application.ExpenseService;
import com.volter.shop.modules.expense.web.request.ExpenseCreationRequest;
import com.volter.shop.modules.expense.web.response.ExpenseSummaryResponse;
import com.volter.shop.modules.expense.web.request.ExpenseFilterRequest;
import com.volter.shop.modules.expense.web.response.ExpenseResponse;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.infrastructure.mapper.ExpenseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAll(ExpenseFilterRequest filters, Pageable pageable) {
        Page<Expense> expenses = expenseService.getAll(filters, pageable);
        return ResponseEntity.ok(expenses.map(expenseMapper::toResponse));
    }

    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryResponse> getSummary(ExpenseFilterRequest filters) {
        ExpenseSummaryResponse summary = expenseService.getAllGrouped(filters);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/create")
    public ResponseEntity<ExpenseResponse> create(@RequestBody ExpenseCreationRequest request) {
        Expense expense = expenseService.create(request);
        return ResponseEntity.ok(expenseMapper.toResponse(expense));
    }
}
