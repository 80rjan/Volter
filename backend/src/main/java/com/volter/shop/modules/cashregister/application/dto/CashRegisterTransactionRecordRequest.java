package com.volter.shop.modules.cashregister.application.dto;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CashRegisterTransactionRecordRequest(
        @NotNull(message = "Action is required") CashRegisterTransactionAction action,
        @NotNull(message = "Direction is required") TransactionDirection direction,
        @NotNull(message = "Amount is required") @PositiveOrZero(message = "Amount cannot be negative") Integer amount,
        String description
) {
}
