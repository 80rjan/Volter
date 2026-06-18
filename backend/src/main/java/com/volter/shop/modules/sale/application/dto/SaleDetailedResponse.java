package com.volter.shop.modules.sale.application.dto;

import com.volter.shop.modules.customer.application.dto.CustomerResponse;
import com.volter.shop.modules.inventory.application.dto.ItemResponse;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;

import java.time.OffsetDateTime;

public record SaleDetailedResponse(
        Long id,
        CustomerResponse customer,
        ItemResponse item,
        Long createdByStaffId,
        SaleStatus status,
        Integer purchasePrice,
        Integer salePrice,
        Integer profit,
        OffsetDateTime soldAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
