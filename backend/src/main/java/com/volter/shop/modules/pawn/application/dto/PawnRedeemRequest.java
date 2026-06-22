package com.volter.shop.modules.pawn.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PawnRedeemRequest(
        @NotNull(message = "Paid amount is required")
        @PositiveOrZero(message = "Paid amount cannot be negative")
        Integer paidAmount,
        @NotNull(message = "Cash register session id is required") Long cashRegisterSessionId
) {
}
