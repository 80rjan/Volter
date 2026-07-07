package com.volter.shop.modules.sale.application.dto;

/**
 * Aggregate totals over the filtered set of sales (typically AVAILABLE), shown
 * as a summary bar under the sales table. Month-to-date profit is no longer part
 * of this response; it lives on the transaction module's
 * {@code /transactions/monthly-profit} endpoint.
 */
public record SaleSummaryResponse(
        long count,
        long totalPurchase,
        double totalGoldGrams
) {
}
