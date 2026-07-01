package com.volter.shop.modules.pawn.application.dto;

/**
 * Aggregate totals over the filtered set of pawn contracts (typically ACTIVE),
 * shown as a summary bar under the pawns table. {@code monthlyProvision} is the
 * provision (interest income) collected from the first of the current month
 * until now, independent of the filter.
 */
public record PawnSummaryResponse(
        long count,
        long totalPrincipal,
        long totalInterest,
        double totalGoldGrams,
        long monthlyProvision
) {
}
