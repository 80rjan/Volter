package com.volter.shop.modules.sale.application.dto;

/**
 * Aggregate totals over the filtered set of sales (typically AVAILABLE), shown
 * as a summary bar under the sales table. {@code monthlyProfit} is the profit
 * (sale price minus purchase price) on sales sold from the first of the current
 * month until now, independent of the filter.
 */
public record SaleSummaryResponse(
        long count,
        long totalPurchase,
        double totalGoldGrams,
        long monthlyProfit
) {
}
