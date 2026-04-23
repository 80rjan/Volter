package com.volter.shop.modules.cashregister.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CashRegisterSessionDepositRequest (
        @NotNull(message = "Deposit amount is required") Integer depositAmount,
        @NotBlank(message = "Transaction description is required") String transactionDescription
) {}
