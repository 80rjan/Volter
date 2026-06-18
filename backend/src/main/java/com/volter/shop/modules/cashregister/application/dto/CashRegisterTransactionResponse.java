package com.volter.shop.modules.cashregister.application.dto;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;

import java.time.OffsetDateTime;

public record CashRegisterTransactionResponse(
        Long id,
        Long transactionId,
        Long sessionId,
        Long staffId,
        CashRegisterTransactionAction action,
        Integer amount,
        TransactionDirection direction,
        String description,
        OffsetDateTime createdAt
) {
}
