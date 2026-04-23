package com.volter.shop.modules.sale.application.dto.filter;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;

public record SaleFilter (
        SaleStatus status,
        Boolean active,

        ItemType itemType,

        String customerName,
        String customerEmbg,
        String customerPhoneNumber
) {}
