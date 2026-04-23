package com.volter.shop.modules.pawn.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PawnForfeitureRequest(
        @NotBlank(message = "Transaction description is required") String transactionDescription
) {
}
