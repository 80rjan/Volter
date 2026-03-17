package com.volter.backend.expense.dto;

import com.volter.backend.expense.enums.ExpenseType;

import java.util.Map;

public record ExpenseSummaryDTO (
        Integer month,
        Integer year,
        Map<ExpenseType, Integer> totalByType,
        Integer grandTotal
) {}
