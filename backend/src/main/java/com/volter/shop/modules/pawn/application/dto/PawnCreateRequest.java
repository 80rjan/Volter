package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.inventory.application.dto.ItemCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record PawnCreateRequest(
        @NotNull(message = "Customer id is required") Long customerId,
        @NotNull(message = "Item is required") @Valid ItemCreateRequest item,
        @NotNull(message = "Principal amount is required") @Positive(message = "Principal must be positive") Integer principalAmount,
        @NotNull(message = "Interest amount is required") @PositiveOrZero(message = "Interest cannot be negative") Integer interestAmount,
        @NotNull(message = "Term in days is required") @Min(value = 1, message = "Term must be at least 1 day") Integer termDays,
        @NotNull(message = "Issue date is required") LocalDate issueDate,
        @NotNull(message = "Cash register session id is required") Long cashRegisterSessionId
) {
}
