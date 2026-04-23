package com.volter.shop.modules.inventory.application.dtos.baseitem.request;

import java.math.BigDecimal;

public record ItemModificationRequest(
        String description,
        BigDecimal goldWeightGrams
) {
}
