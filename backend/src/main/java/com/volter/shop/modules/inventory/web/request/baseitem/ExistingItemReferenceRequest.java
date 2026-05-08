package com.volter.shop.modules.inventory.web.request.baseitem;

import com.volter.shop.modules.inventory.domain.model.enums.ItemReferenceStrategy;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExistingItemReferenceRequest implements ItemReferenceRequest {

    @NotNull(message = "Item ID is required for existing item source")
    private Long itemId;

    @Override
    public ItemReferenceStrategy getReferenceStrategy() {
        return ItemReferenceStrategy.EXISTING;
    }
}
