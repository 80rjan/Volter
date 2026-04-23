package com.volter.shop.modules.sale.application.dto.result;


import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import jakarta.validation.constraints.NotNull;

public record SaleSellResult(
        boolean underpaid,
        @NotNull(message = "Sale transaction is required") SaleTransaction transaction
) {
}
