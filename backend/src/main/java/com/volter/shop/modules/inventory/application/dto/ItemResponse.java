package com.volter.shop.modules.inventory.application.dto;

import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;

import java.time.OffsetDateTime;
import java.util.Map;

public record ItemResponse(
        Long id,
        ItemType type,
        ItemOriginType origin,
        ItemStatus status,
        String description,
        Map<String, Object> attributes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
