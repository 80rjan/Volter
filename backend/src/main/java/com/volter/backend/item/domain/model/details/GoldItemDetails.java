package com.volter.backend.item.domain.model.details;

import com.volter.backend.item.domain.model.enums.details.GoldItemCarats;
import com.volter.backend.item.domain.model.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        indexes = {
                @Index(name = "idx_gold_item_details_carats", columnList = "carats"),
                @Index(name = "idx_gold_item_details_piece_type", columnList = "pieceType")
        }
)
public class GoldItemDetails {

    @Id
    private Long itemId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_gold_item_details_item"))
    private Item item;

    @NotNull(message = "Gold item weight in grams is required")
    @Column(nullable = false)
    private Float weightGrams;

    @NotNull(message = "Gold item price per gram is required")
    @Column(nullable = false)
    private Float pricePerGram;

    @NotNull(message = "Gold item carats is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoldItemCarats carats;

    @NotNull(message = "Gold item piece type is required")
    @Column(nullable = false)
    private String pieceType;
}
