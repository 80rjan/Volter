package com.volter.shop.modules.transaction.application.dto;

import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;

import java.time.OffsetDateTime;

public record TransactionResponse(
        Long id,
        Long staffId,
        Long cashRegisterSessionId,
        TransactionType type,
        Integer amount,
        TransactionDirection direction,
        String description,
        String clientName,
        OffsetDateTime createdAt
) {
}
