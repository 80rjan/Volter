package com.volter.shop.modules.expense.application.dto.response;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        @NotNull(message = "Expense id is required") Long id,
        @NotNull(message = "Expense type is required") ExpenseType expenseType,
        @NotNull(message = "Expense amount is required") Integer amount,
        @NotBlank(message = "Expense description is required") String description,
        @NotNull(message = "Expense date is required") LocalDate date,
        @NotNull(message = "Expense created at is required") LocalDateTime createdAt,
        @NotNull(message = "Expense updated at is required") LocalDateTime updatedAt
) {
}
