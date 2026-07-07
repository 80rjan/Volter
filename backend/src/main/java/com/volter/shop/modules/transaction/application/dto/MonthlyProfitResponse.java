package com.volter.shop.modules.transaction.application.dto;

/**
 * Profit earned from the first of the current month until now, derived from the
 * transaction ledger: {@code pawnProvision} is the pawn interest income,
 * {@code saleProfit} is the sale margin (sale price minus purchase price) and
 * {@code totalExpenses} is the shop's expenses over the same period.
 * {@code netProfit} is the final figure: provision + sale profit − expenses.
 */
public record MonthlyProfitResponse(
        long pawnProvision,
        long saleProfit,
        long totalExpenses,
        long netProfit
) {
}
