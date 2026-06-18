package com.volter.shop.modules.expense.application.dto;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ExpenseCreateRequest(
        @NotNull(message = "Category is required") ExpenseCategory category,
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") Integer amount,
        String description,
        @NotNull(message = "Date is required") LocalDate date,
        @NotNull(message = "Cash register session id is required") Long cashRegisterSessionId
) {
}
