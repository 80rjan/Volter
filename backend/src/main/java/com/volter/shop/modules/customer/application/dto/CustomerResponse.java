package com.volter.shop.modules.customer.application.dto;

import java.time.OffsetDateTime;

public record CustomerResponse(
        Long id,
        String fullName,
        String nationalId,
        String phonePrimary,
        String phoneSecondary,
        String address,
        String city,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
