package com.volter.shop.modules.sale.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaleSellRequest(
        @NotNull(message = "Sold price is required") Integer soldPrice,
        @NotBlank(message = "Transaction description is required") String transactionDescription
) {
}
