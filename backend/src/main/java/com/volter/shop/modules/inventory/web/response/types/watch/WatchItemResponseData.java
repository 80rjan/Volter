package com.volter.shop.modules.inventory.web.response.types.watch;

import com.volter.shop.modules.inventory.web.response.baseitem.ItemResponseData;
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
public class WatchItemResponseData implements ItemResponseData {

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Material is required")
    private String material;

    @NotNull(message = "Year is required")
    private Integer year;

    @Override
    public ItemType getItemType() {
        return ItemType.WATCH;
    }
}
