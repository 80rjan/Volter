package com.volter.backend.item.domain.model.details;

import com.volter.backend.item.domain.model.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
            indexes = {
                    @Index(name = "idx_watch_item_details_brand", columnList = "brand"),
                    @Index(name = "idx_watch_item_details_model", columnList = "model"),
                    @Index(name = "idx_watch_item_details_year", columnList = "year"),
                    @Index(name = "idx_watch_item_details_functional", columnList = "functional"),
                    @Index(name = "idx_watch_item_details_service_required", columnList = "serviceRequired"),
                    @Index(name = "idx_watch_item_details_original_box_included", columnList = "originalBoxIncluded"),
                    @Index(name = "idx_watch_item_details_original_papers_included", columnList = "originalPapersIncluded"),
                    @Index(name = "idx_watch_item_details_warranty_card_included", columnList = "warrantyCardIncluded")
            }
)
public class WatchItemDetails {

    @Id
    private Long itemId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_watch_item_details_item"))
    private Item item;

    @NotNull(message = "Watch brand is required")
    @Column(nullable = false)
    private String brand;

    @NotNull(message = "Watch model is required")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Watch material is required")
    @Column(nullable = false)
    private String material;

    @NotNull(message = "Watch year is required")
    @Column(nullable = false)
    private Integer year;

    @NotNull(message = "Watch is original box included is required")
    @Column(nullable = false)
    private boolean originalBoxIncluded;

    @NotNull(message = "Watch is originals papers included is required")
    @Column(nullable = false)
    private boolean originalPapersIncluded;

    @NotNull(message = "Watch is warranty card included is required")
    @Column(nullable = false)
    private boolean warrantyCardIncluded;

    @Column(nullable = true)
    private LocalDate warrantyExpirationDate;

    @NotNull(message = "Watch is functional is required")
    @Column(nullable = false)
    private boolean functional;

    @NotNull(message = "Watch is service required is required")
    @Column(nullable = false)
    private boolean serviceRequired;
}
