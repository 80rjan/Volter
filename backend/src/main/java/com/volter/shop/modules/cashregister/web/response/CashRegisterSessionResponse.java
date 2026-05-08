package com.volter.shop.modules.cashregister.web.response;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CashRegisterSessionResponse(
        @NotNull(message = "Opened at is required") LocalDateTime openedAt,
        @NotNull(message = "Updated at is required") LocalDateTime updatedAt,
        @NotNull(message = "Opening balance is required") Integer openingBalance,
        @NotNull(message = "Current balance is required") Integer currentBalance,
        @NotNull(message = "Expected pawn interest is required") Integer expectedPawnInterest
) {
}