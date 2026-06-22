package com.volter.shop.modules.expense.application.dto;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;

import java.util.Map;

/**
 * Expenses for one calendar month: the grand total and count, plus the total
 * amount broken down per expense category.
 */
public record ExpenseMonthlySummary(
        int year,
        int month,
        long totalAmount,
        long count,
        Map<ExpenseCategory, Long> totalsByCategory
) {
}
