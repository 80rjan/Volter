package com.volter.shop.modules.transaction.application.dto;

/**
 * Profit earned from the first of the current month until now, derived from the
 * transaction ledger: {@code pawnProvision} is the pawn interest income and
 * {@code saleProfit} is the sale margin (sale price minus purchase price), with
 * {@code total} their sum.
 */
public record MonthlyProfitResponse(
        long pawnProvision,
        long saleProfit,
        long total
) {
}
