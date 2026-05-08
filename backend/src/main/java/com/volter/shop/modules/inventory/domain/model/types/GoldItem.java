package com.volter.shop.modules.inventory.domain.model.types;

import com.volter.shop.modules.inventory.web.request.baseitem.ItemModificationRequest;
import com.volter.shop.modules.inventory.domain.model.enums.types.GoldItemCarats;
import com.volter.shop.modules.inventory.domain.model.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@DiscriminatorValue("GOLD")
public class GoldItem extends Item {

    @NotNull(message = "Gold item weight in grams is required")
    @Column(nullable = false)
    private BigDecimal weightGrams;

    @NotNull(message = "Gold item price per gram is required")
    @Column(nullable = false)
    private Integer pricePerGram;

    @NotNull(message = "Gold item carats is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoldItemCarats carats;

    @NotBlank(message = "Gold item piece type is required")
    @Column(nullable = false)
    private String pieceType;

    @Override
    public void modify(ItemModificationRequest request) {
        super.modify(request);

        BigDecimal totalPrice = BigDecimal.valueOf(pricePerGram).multiply(weightGrams);

        BigDecimal newWeight = request.goldWeightGrams();
        this.weightGrams = newWeight;
        this.pricePerGram = totalPrice.divide(newWeight, 0, RoundingMode.HALF_UP).intValue();
    }
}
