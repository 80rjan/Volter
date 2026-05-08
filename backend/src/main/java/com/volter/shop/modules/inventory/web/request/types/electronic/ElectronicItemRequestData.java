package com.volter.shop.modules.inventory.web.request.types.electronic;

import com.volter.shop.modules.inventory.web.request.baseitem.ItemRequestData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ElectronicItemRequestData implements ItemRequestData {

    @NotNull(message = "Item origin type is required")
    private ItemOriginType itemOriginType;

    @NotNull(message = "Item status is required")
    private ItemStatus itemStatus;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private Integer year;

    @Override
    public ItemType getItemType() {
        return ItemType.ELECTRONIC;
    }
}