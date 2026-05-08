package com.volter.shop.modules.inventory.web.request.baseitem;

import com.volter.shop.modules.inventory.domain.model.enums.ItemReferenceStrategy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewItemReferenceRequest implements ItemReferenceRequest {

    @NotNull(message = "Item data is required for new item source")
    @Valid
    private ItemRequestData data;

    @Override
    public ItemReferenceStrategy getReferenceStrategy() {
        return ItemReferenceStrategy.NEW;
    }
}
