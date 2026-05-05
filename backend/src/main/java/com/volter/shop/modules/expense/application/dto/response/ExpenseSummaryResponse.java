package com.volter.shop.modules.expense.application.dto.response;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;
import com.volter.shop.shared.valueobject.Money;

import java.time.LocalDate;
import java.util.Map;

public record ExpenseSummaryResponse (
        LocalDate fromDate,
        LocalDate toDate,
        Map<ExpenseType, Money> totalByType,
        Money grandTotal
) {}
