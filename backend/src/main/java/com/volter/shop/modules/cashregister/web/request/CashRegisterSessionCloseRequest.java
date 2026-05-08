package com.volter.shop.modules.cashregister.web.request;

import jakarta.validation.constraints.NotNull;

public record CashRegisterSessionCloseRequest(
        @NotNull(message = "Closing balance is required") Integer closingBalance
) {
}
