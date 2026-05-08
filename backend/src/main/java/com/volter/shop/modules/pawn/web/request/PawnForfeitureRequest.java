package com.volter.shop.modules.pawn.web.request;

import jakarta.validation.constraints.NotBlank;

public record PawnForfeitureRequest(
        @NotBlank(message = "Transaction description is required") String transactionDescription
) {
}
