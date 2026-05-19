package com.volter.shop.modules.pawn.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PawnRedemptionRequest(
        @NotNull(message = "Paid amount in request needed") Integer paidAmount,
        String transactionDescription
) {
}
