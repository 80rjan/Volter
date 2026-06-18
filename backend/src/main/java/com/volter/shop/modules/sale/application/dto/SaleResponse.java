package com.volter.shop.modules.sale.application.dto;

import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;

import java.time.OffsetDateTime;

public record SaleResponse(
        Long id,
        Long customerId,
        String customerName,
        Long itemId,
        Long createdByStaffId,
        SaleStatus status,
        Integer purchasePrice,
        Integer salePrice,
        Integer profit,
        OffsetDateTime soldAt,
        OffsetDateTime createdAt
) {
}
