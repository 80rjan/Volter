package com.volter.shop.modules.sale.application.dto;

/**
 * Aggregate totals over the filtered set of sales (typically AVAILABLE), shown
 * as a summary bar under the sales table.
 */
public record SaleSummaryResponse(
        long count,
        long totalPurchase,
        double totalGoldGrams
) {
}
