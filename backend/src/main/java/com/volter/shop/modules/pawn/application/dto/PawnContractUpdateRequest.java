package com.volter.shop.modules.pawn.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

/**
 * Edit the terms of an ACTIVE pawn contract. Changing {@code principalAmount}
 * moves cash (and so requires an open cash register session): raising it pays the
 * customer the difference (OUT), lowering it takes the difference back (IN).
 * Changing {@code termDays} or {@code issueDate} shifts the due date accordingly
 * (the due date is not set directly). Interest is recorded for
 * redemption/extension and moves no cash now.
 */
public record PawnContractUpdateRequest(
        @NotNull(message = "Principal amount is required") @PositiveOrZero Integer principalAmount,
        @NotNull(message = "Interest amount is required") @PositiveOrZero Integer interestAmount,
        @NotNull(message = "Term in days is required") @Min(1) Integer termDays,
        @NotNull(message = "Issue date is required") LocalDate issueDate,
        // Required only when the principal changes (to move the cash).
        Long cashRegisterSessionId
) {
}
