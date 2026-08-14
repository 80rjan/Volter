package com.volter.shop.modules.pawn.application.dto;

/**
 * Aggregate totals over the filtered set of pawn contracts (typically ACTIVE),
 * shown as a summary bar under the pawns table.
 *
 * <p>Alongside the totals, two month-scoped figures give a "this month" view:
 * <ul>
 *   <li>{@code monthlyPrincipal} — principal handed out for pawns <em>opened</em>
 *       this month (by issue date).</li>
 *   <li>{@code monthlyInterest} — interest still to collect on pawns that
 *       <em>expire by the end</em> of this month (due date on/before month end).</li>
 * </ul>
 * Month-to-date realized provision is a different figure and lives on the
 * transaction module's {@code /transactions/monthly-profit} endpoint.
 */
public record PawnSummaryResponse(
        long count,
        long totalPrincipal,
        long monthlyPrincipal,
        long totalInterest,
        long monthlyInterest,
        double totalGoldGrams
) {
}
