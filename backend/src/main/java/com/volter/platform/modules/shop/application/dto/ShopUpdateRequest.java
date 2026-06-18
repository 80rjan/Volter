package com.volter.platform.modules.shop.application.dto;

import jakarta.validation.constraints.Size;

public record ShopUpdateRequest(
        String address,
        @Size(max = 32) String phone
) {
}
