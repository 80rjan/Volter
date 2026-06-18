package com.volter.shop.modules.inventory.application.dto;

import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ItemCreateRequest(
        @NotNull(message = "Item type is required") ItemType type,
        @NotNull(message = "Origin is required") ItemOriginType origin,
        @NotNull(message = "Initial status is required") ItemStatus initialStatus,
        String description,
        Map<String, Object> attributes
) {
}
