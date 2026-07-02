package com.volter.shop.modules.bonus.application.dto;

import java.math.BigDecimal;

/**
 * A staff member's profit-share bonus ledger for the current calendar month
 * (from the 1st until now — it resets monthly).
 *
 * <ul>
 *   <li>{@code profitBase} — net profit base: their own (pawn provision + sale margin) minus the shop's expenses</li>
 *   <li>{@code earned}     — {@code profitSharePercent}% of {@code profitBase}</li>
 *   <li>{@code taken}      — bonus already withdrawn this month</li>
 *   <li>{@code available}  — {@code earned − taken}, floored at 0 (what they may take now)</li>
 * </ul>
 */
public record StaffBonusAvailableResponse(
        Long staffId,
        BigDecimal profitSharePercent,
        long profitBase,
        long earned,
        long taken,
        long available
) {
}
