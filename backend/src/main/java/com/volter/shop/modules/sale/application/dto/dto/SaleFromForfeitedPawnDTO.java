package com.volter.shop.modules.sale.application.dto.dto;

import com.volter.shop.modules.sale.domain.model.enums.SaleTransactionAction;
import jakarta.validation.constraints.NotNull;

public record SaleFromForfeitedPawnDTO(
        @NotNull(message = "Sale creation action is required") SaleTransactionAction creationAction,
        @NotNull(message = "Purchase price is required") Integer purchasePrice
) {
}
