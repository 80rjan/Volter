package com.volter.platform.modules.shop.application.dto;

import com.volter.platform.modules.shop.domain.model.enums.ShopStatus;

public record ShopFilterRequest(
        String name,
        String code,
        ShopStatus status
) {
}
