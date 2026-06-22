package com.volter.shop.modules.transaction.application.dto;

import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;

import java.time.OffsetDateTime;

public record TransactionFilterRequest(
        TransactionType type,
        TransactionDirection direction,
        Long staffId,
        OffsetDateTime createdFrom,
        OffsetDateTime createdTo
) {
}
