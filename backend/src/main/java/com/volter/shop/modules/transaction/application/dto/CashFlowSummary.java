package com.volter.shop.modules.transaction.application.dto;

public record CashFlowSummary(
        Long totalTransactions,
        Long inflow,
        Long outflow,
        Long net
) {
}
