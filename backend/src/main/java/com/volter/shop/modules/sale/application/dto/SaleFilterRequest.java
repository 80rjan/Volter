package com.volter.shop.modules.sale.application.dto;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;

import java.time.OffsetDateTime;

public record SaleFilterRequest(
        SaleStatus status,
        String customerFullName,
        String customerNationalId,
        String customerPhone,
        ItemType itemType,
        Long createdByStaffId,
        OffsetDateTime soldFrom,
        OffsetDateTime soldTo
) {
}
