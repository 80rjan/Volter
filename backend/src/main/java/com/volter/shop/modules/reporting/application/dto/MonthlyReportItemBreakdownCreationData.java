package com.volter.shop.modules.reporting.application.dto;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.validation.constraints.NotNull;

public record MonthlyReportItemBreakdownCreationData(
        @NotNull(message = "Category is required") TransactionCategory category,
        @NotNull(message = "Type is required") ItemType type,
        @NotNull(message = "Count is required") Integer count,
        @NotNull(message = "Total turnover is required") Integer turnover,
        @NotNull(message = "Total revenue is required") Integer revenue,
        @NotNull(message = "Total cash out is required") Integer cashOut,
        @NotNull(message = "Gross profit is required") Integer grossProfit,
        @NotNull(message = "Total expenses is required") Integer expenses,
        @NotNull(message = "Net profit is required") Integer netProfit
) {
}
