package com.volter.shop.modules.inventory.web.response.baseitem;

import com.volter.shop.modules.inventory.domain.model.Item;
import jakarta.validation.constraints.NotNull;

public record ItemFactoryResult(
        @NotNull(message = "Item is required") Item item,
        boolean isNewItem
) {
}
