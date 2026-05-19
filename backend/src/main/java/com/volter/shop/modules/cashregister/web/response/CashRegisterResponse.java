package com.volter.shop.modules.cashregister.web.response;

import jakarta.validation.constraints.NotNull;

public record CashRegisterResponse(
        @NotNull(message = "Id is required") Long id,
        @NotNull(message = "Code is required") String code
) {}
