package com.volter.shop.modules.pawn.application.dto;

/**
 * Aggregate totals over the filtered set of pawn contracts (typically ACTIVE),
 * shown as a summary bar under the pawns table. Month-to-date provision is no
 * longer part of this response; it lives on the transaction module's
 * {@code /transactions/monthly-profit} endpoint.
 */
public record PawnSummaryResponse(
        long count,
        long totalPrincipal,
        long totalInterest,
        double totalGoldGrams
) {
}
