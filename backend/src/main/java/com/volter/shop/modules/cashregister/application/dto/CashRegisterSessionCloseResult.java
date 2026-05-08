package com.volter.shop.modules.cashregister.application.dto;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyType;
import com.volter.shop.shared.valueobject.Money;

public record CashRegisterSessionCloseResult(
        Money discrepancy,
        CashRegisterSessionDiscrepancyType discrepancyType
) {
}
