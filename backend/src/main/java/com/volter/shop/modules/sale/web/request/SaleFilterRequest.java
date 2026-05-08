package com.volter.shop.modules.sale.web.request;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;

public record SaleFilterRequest(
        SaleStatus status,
        Boolean active,

        ItemType itemType,

        String customerName,
        String customerEmbg,
        String customerPhoneNumber
) {}
