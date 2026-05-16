package com.volter.shop.modules.inventory.web.response.types.other;

import com.volter.shop.modules.inventory.web.response.baseitem.ItemResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtherItemResponseData implements ItemResponseData {

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @Override
    public ItemType getItemType() {
        return ItemType.OTHER;
    }
}
