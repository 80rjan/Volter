package com.volter.shop.modules.cashregister.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CashRegisterCreateRequest(
        @NotBlank(message = "Code is required") @Size(max = 50) String code
) {
}
