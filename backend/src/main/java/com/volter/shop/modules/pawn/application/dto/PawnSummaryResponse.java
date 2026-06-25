package com.volter.shop.modules.pawn.application.dto;

/**
 * Aggregate totals over the filtered set of pawn contracts (typically ACTIVE),
 * shown as a summary bar under the pawns table.
 */
public record PawnSummaryResponse(
        long count,
        long totalPrincipal,
        long totalInterest,
        double totalGoldGrams
) {
}
