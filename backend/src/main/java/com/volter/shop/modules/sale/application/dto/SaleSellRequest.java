package com.volter.shop.modules.sale.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SaleSellRequest(
        @NotNull(message = "Sale price is required")
        @PositiveOrZero(message = "Sale price cannot be negative") Integer salePrice,
        @NotNull(message = "Cash register session id is required") Long cashRegisterSessionId
) {
}
