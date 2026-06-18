package com.volter.shop.modules.pawn.application.dto;

import jakarta.validation.constraints.NotNull;

public record PawnRedeemRequest(
        @NotNull(message = "Cash register session id is required") Long cashRegisterSessionId
) {
}
