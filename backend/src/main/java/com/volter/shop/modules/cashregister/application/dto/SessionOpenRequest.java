package com.volter.shop.modules.cashregister.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SessionOpenRequest(
        @NotNull(message = "Opening balance is required") Integer openingBalance
) {
}
