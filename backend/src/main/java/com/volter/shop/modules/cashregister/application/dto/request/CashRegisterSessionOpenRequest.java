package com.volter.shop.modules.cashregister.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record CashRegisterSessionOpenRequest(
        @NotNull(message = "Opening balance is required") Integer openingBalance,
        @NotNull(message = "Cash register id is required") Long cashRegisterId
) {}
