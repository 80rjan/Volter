package com.volter.shop.modules.expense.application.dto;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;

import java.time.LocalDate;

public record ExpenseFilterRequest(
        ExpenseCategory category,
        Long staffId,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}
