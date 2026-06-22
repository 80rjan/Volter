package com.volter.shop.modules.pawn.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Edit the terms of an ACTIVE pawn contract. Changing {@code principalAmount}
 * moves cash (and so requires an open cash register session): raising it pays the
 * customer the difference (OUT), lowering it takes the difference back (IN).
 * Changing {@code termDays} shifts the due date by the same number of days.
 * Interest is recorded for redemption/extension and moves no cash now.
 */
public record PawnContractUpdateRequest(
        @NotNull(message = "Principal amount is required") @PositiveOrZero Integer principalAmount,
        @NotNull(message = "Interest amount is required") @PositiveOrZero Integer interestAmount,
        @NotNull(message = "Term in days is required") @Min(1) Integer termDays,
        // Required only when the principal changes (to move the cash).
        Long cashRegisterSessionId
) {
}
