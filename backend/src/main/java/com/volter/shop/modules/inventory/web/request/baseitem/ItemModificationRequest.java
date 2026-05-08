package com.volter.shop.modules.inventory.web.request.baseitem;

import java.math.BigDecimal;

public record ItemModificationRequest(
        String description,
        BigDecimal goldWeightGrams
) {
}
