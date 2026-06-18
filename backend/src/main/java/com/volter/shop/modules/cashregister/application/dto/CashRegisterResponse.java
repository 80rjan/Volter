package com.volter.shop.modules.cashregister.application.dto;

import java.time.OffsetDateTime;

public record CashRegisterResponse(
        Long id,
        String code,
        OffsetDateTime createdAt
) {
}
