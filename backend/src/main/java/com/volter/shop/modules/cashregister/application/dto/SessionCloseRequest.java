package com.volter.shop.modules.cashregister.application.dto;

import jakarta.validation.constraints.NotNull;

public record SessionCloseRequest(
        @NotNull(message = "Counted closing balance is required") Integer countedClosingBalance
) {
}
