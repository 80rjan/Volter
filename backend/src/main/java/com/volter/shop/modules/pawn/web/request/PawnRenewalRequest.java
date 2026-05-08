package com.volter.shop.modules.pawn.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PawnRenewalRequest(
        @NotBlank(message = "Transaction description is required") String transactionDescription,
        @NotNull(message = "Interest is required") Integer interest
) {
}
