package com.volter.shop.modules.cashregister.application.dto;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyStatus;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyType;

public record DiscrepancyFilterRequest(
        Long sessionId,
        Long staffId,
        CashRegisterSessionDiscrepancyType type,
        CashRegisterSessionDiscrepancyStatus status
) {
}
