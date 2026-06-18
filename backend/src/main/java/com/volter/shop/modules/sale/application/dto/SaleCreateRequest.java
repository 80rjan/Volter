package com.volter.shop.modules.sale.application.dto;

import com.volter.shop.modules.inventory.application.dto.ItemCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SaleCreateRequest(
        @NotNull(message = "Customer id is required") Long customerId,
        @NotNull(message = "Item is required") @Valid ItemCreateRequest item,
        @NotNull(message = "Purchase price is required")
        @PositiveOrZero(message = "Purchase price cannot be negative") Integer purchasePrice,
        @NotNull(message = "Cash register session id is required") Long cashRegisterSessionId
) {
}
