package com.volter.shop.modules.bonus.application.dto;

import java.math.BigDecimal;

/**
 * A staff member's profit-share bonus ledger in the current shop, from their
 * baseline ({@code staff_shop.bonus_since}) until now.
 *
 * <ul>
 *   <li>{@code profitBase} — profit they generated (pawn provision + sale margin)</li>
 *   <li>{@code earned}     — {@code profitSharePercent}% of {@code profitBase}</li>
 *   <li>{@code taken}      — bonus already withdrawn</li>
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
