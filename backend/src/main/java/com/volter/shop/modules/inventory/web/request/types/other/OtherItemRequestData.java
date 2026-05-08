package com.volter.shop.modules.inventory.web.request.types.other;

import com.volter.shop.modules.inventory.web.request.baseitem.ItemRequestData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OtherItemRequestData implements ItemRequestData {

    @NotNull(message = "Item origin type is required")
    private ItemOriginType itemOriginType;

    @NotNull(message = "Item status is required")
    private ItemStatus itemStatus;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @Override
    public ItemType getItemType() {
        return ItemType.OTHER;
    }
}