package com.volter.shop.modules.cashregister.application.dto;

public record CashRegisterSummary(
        Long totalTransactions,
        Long inflow,
        Long outflow,
        Long net,
        Long withdrawals,
        Long deposits,
        Long adjustments
) {
}
