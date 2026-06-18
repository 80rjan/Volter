package com.volter.identity.modules.auth.application.dto;

import jakarta.validation.constraints.NotNull;

public record SelectShopRequest(
        @NotNull(message = "Shop id is required") Long shopId
) {
}
