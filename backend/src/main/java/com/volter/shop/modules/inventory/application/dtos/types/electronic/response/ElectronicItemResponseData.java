package com.volter.shop.modules.inventory.application.dtos.types.electronic.response;

import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElectronicItemResponseData implements ItemResponseData {
    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Year is required")
    private Integer year;

    @Override
    public ItemType getItemType() {
        return ItemType.ELECTRONIC;
    }
}
