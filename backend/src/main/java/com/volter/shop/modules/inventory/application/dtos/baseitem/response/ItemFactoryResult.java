package com.volter.shop.modules.inventory.application.dtos.baseitem.response;

import com.volter.shop.modules.inventory.domain.model.Item;
import jakarta.validation.constraints.NotNull;

public record ItemFactoryResult(
        @NotNull(message = "Item is required") Item item,
        boolean isNewItem
) {
}
