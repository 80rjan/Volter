package com.volter.backend.expense.application.dto;

import com.volter.backend.expense.domain.model.enums.ExpenseType;

import java.util.Map;

public record ExpenseSummaryDTO (
        Integer month,
        Integer year,
        Map<ExpenseType, Integer> totalByType,
        Integer grandTotal
) {}
