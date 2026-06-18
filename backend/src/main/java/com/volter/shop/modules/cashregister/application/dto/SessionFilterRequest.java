package com.volter.shop.modules.cashregister.application.dto;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;

import java.time.OffsetDateTime;

public record SessionFilterRequest(
        Long cashRegisterId,
        Long staffId,
        CashRegisterSessionStatus status,
        OffsetDateTime openedFrom,
        OffsetDateTime openedTo
) {
}
