package com.volter.shop.modules.expense.application.dto;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;

/**
 * Flat aggregation row: total and count of expenses for one (year, month,
 * category) bucket. Assembled into {@link ExpenseMonthlySummary} by the service.
 */
public record ExpenseCategoryBucket(
        Integer year,
        Integer month,
        ExpenseCategory category,
        Long total,
        Long count
) {
}
