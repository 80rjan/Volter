package com.volter.shop.modules.inventory.web.request.types.gold;

import com.volter.shop.modules.inventory.web.request.baseitem.ItemRequestData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.inventory.domain.model.enums.types.GoldItemCarats;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoldItemRequestData implements ItemRequestData {

    @NotNull(message = "Item origin type is required")
    private ItemOriginType itemOriginType;

    @NotNull(message = "Item status is required")
    private ItemStatus itemStatus;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Weight in grams is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weightGrams;

    @NotNull(message = "Price per gram is required")
    @Positive(message = "Price per gram must be positive")
    private Integer pricePerGram;

    @NotNull(message = "Carats is required")
    private GoldItemCarats carats;

    @NotBlank(message = "Piece type is required")
    private String pieceType;

    @Override
    public ItemType getItemType() {
        return ItemType.GOLD;
    }
}