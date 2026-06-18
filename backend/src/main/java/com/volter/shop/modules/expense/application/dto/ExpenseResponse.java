package com.volter.shop.modules.expense.application.dto;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ExpenseResponse(
        Long id,
        Long staffId,
        String staffName,
        ExpenseCategory category,
        Integer amount,
        String description,
        LocalDate date,
        OffsetDateTime createdAt
) {
}
