package com.volter.shop.modules.inventory.application.dtos.types.gold.response;

import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.inventory.domain.model.enums.types.GoldItemCarats;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoldItemResponseData implements ItemResponseData {

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Weight in grams is required")
    private BigDecimal weightGrams;

    @NotNull(message = "Carats is required")
    private GoldItemCarats carats;

    @NotBlank(message = "Piece type is required")
    private String pieceType;

    @Override
    public ItemType getItemType() {
        return ItemType.GOLD;
    }
}
