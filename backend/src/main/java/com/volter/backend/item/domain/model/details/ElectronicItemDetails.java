package com.volter.backend.item.domain.model.details;

import com.volter.backend.item.domain.model.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_electronic_item_details_brand", columnList = "brand"),
                @Index(name = "idx_electronic_item_details_category", columnList = "category"),
                @Index(name = "idx_electronic_item_details_year", columnList = "year")
        }
)
public class ElectronicItemDetails {

    @Id
    private Long itemId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_electronic_item_details_item"))
    private Item item;

    @NotNull(message = "Electronic brand is required")
    @Column(nullable = false)
    private String brand;

    @NotNull(message = "Electronic category is required")
    @Column(nullable = false)
    private String category;

    @NotNull(message = "Electronic year is required")
    @Column(nullable = false)
    private Integer year;
}
