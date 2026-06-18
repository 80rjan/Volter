package com.volter.shop.modules.cashregister.application.dto;

import java.time.LocalDate;

public record CashRegisterSessionSummary(
        Long sessionId,
        LocalDate date,
        Long totalTransactions,
        Long inflow,
        Long outflow,
        Long net
) {
}
