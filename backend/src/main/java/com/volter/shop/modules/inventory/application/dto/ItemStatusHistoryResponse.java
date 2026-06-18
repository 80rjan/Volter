package com.volter.shop.modules.inventory.application.dto;

import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;

import java.time.OffsetDateTime;

public record ItemStatusHistoryResponse(
        Long id,
        Long itemId,
        Long changedByStaffId,
        String changedByStaffName,
        ItemStatus fromStatus,
        ItemStatus toStatus,
        OffsetDateTime occurredAt
) {
}
