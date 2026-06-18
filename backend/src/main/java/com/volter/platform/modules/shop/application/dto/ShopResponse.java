package com.volter.platform.modules.shop.application.dto;

import com.volter.platform.modules.shop.domain.model.enums.ShopStatus;

import java.time.OffsetDateTime;

public record ShopResponse(
        Long id,
        String name,
        String code,
        String schemaName,
        String address,
        String phone,
        ShopStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
