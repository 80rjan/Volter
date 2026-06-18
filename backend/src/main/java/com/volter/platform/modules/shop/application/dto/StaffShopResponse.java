package com.volter.platform.modules.shop.application.dto;

import com.volter.platform.modules.shop.domain.model.enums.StaffShopStatus;

import java.time.OffsetDateTime;

public record StaffShopResponse(
        Long id,
        Long staffId,
        Long shopId,
        String shopName,
        StaffShopStatus status,
        OffsetDateTime assignedAt,
        OffsetDateTime unassignedAt
) {
}
