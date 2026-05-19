package com.volter.shop.modules.pawn.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PawnRenewalRequest(
        @NotNull(message = "Interest is required") Integer interest,
        String transactionDescription
) {
}
