package com.volter.shop.modules.expense.application.dto;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;

import java.util.Map;

public record ExpenseSummaryDTO (
        Integer month,
        Integer year,
        Map<ExpenseType, Integer> totalByType,
        Integer grandTotal
) {}
