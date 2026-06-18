package com.volter.shop.modules.cashregister.application.dto;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;

import java.time.OffsetDateTime;

public record CashRegisterSessionResponse(
        Long id,
        Long cashRegisterId,
        String cashRegisterCode,
        Long staffId,
        OffsetDateTime openedAt,
        OffsetDateTime closedAt,
        Integer openingBalance,
        Integer currentBalance,
        Integer closingBalance,
        Integer expectedInterest,
        CashRegisterSessionStatus status
) {
}
