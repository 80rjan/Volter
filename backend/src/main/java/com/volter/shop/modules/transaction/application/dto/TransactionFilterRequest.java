package com.volter.shop.modules.transaction.application.dto;

import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.ActivityType;

import java.time.OffsetDateTime;

public record TransactionFilterRequest(
        ActivityType type,
        TransactionDirection direction,
        Long staffId,
        String clientName,
        OffsetDateTime createdFrom,
        OffsetDateTime createdTo
) {
}
