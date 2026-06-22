package com.volter.shop.modules.cashregister.application.dto;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyPhase;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyStatus;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyType;

import java.time.OffsetDateTime;

public record DiscrepancyResponse(
        Long id,
        Long sessionId,
        Long staffId,
        Long resolvedByStaffId,
        Integer expectedAmount,
        Integer countedAmount,
        Integer difference,
        CashRegisterSessionDiscrepancyType type,
        CashRegisterSessionDiscrepancyPhase phase,
        CashRegisterSessionDiscrepancyStatus status,
        String resolutionNote,
        OffsetDateTime createdAt,
        OffsetDateTime resolvedAt
) {
}
