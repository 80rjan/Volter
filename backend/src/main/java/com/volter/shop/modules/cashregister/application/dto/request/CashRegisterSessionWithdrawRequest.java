package com.volter.shop.modules.cashregister.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CashRegisterSessionWithdrawRequest(
        @NotNull(message = "Deposit amount is required") Integer withdrawAmount,
        @NotBlank(message = "Transaction description is required") String transactionDescription
) {}
