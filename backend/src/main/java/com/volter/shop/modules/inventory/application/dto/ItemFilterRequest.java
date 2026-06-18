package com.volter.shop.modules.inventory.application.dto;

import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;

public record ItemFilterRequest(
        ItemType type,
        ItemStatus status,
        ItemOriginType origin
) {
}
