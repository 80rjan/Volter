package com.volter.shop.modules.pawn.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PawnExtendRequest(
        @NotNull(message = "Interest paid is required") @PositiveOrZero Integer interestPaid,
        @PositiveOrZero(message = "Fee cannot be negative") Integer fee,
        @NotNull(message = "Cash register session id is required") Long cashRegisterSessionId
) {
}
