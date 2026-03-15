package com.volter.backend.item;

import com.volter.backend.electronicItem.ElectronicItemDetails;
import com.volter.backend.goldItem.GoldItemDetails;
import com.volter.backend.item.enums.ItemOriginType;
import com.volter.backend.item.enums.ItemStatus;
import com.volter.backend.item.enums.ItemType;
import com.volter.backend.otherItem.OtherItemDetails;
import com.volter.backend.pawn.Pawn;
import com.volter.backend.sale.Sale;
import com.volter.backend.vehicleItem.VehicleItemDetails;
import com.volter.backend.watchItem.WatchItemDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        indexes = {
                @Index(name = "idx_item_item_type", columnList = "itemType"),
                @Index(name = "idx_item_item_origin_type", columnList = "itemOriginType"),
                @Index(name = "idx_item_item_status", columnList = "itemStatus"),
                @Index(name = "idx_item_created_at_desc", columnList = "createdAt DESC")
        }
)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Item type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    @NotNull(message = "Item origin type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemOriginType itemOriginType;

    @NotNull(message = "Item status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus itemStatus;

    @NotNull(message = "Item description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Item creation timestamp is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Item update timestamp is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = {}, orphanRemoval = false)
    private List<Pawn> pawns = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST}, orphanRemoval = false)
    private List<Sale> sales = new ArrayList<>();

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private GoldItemDetails goldItemDetails;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private ElectronicItemDetails electronicItemDetails;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private VehicleItemDetails vehicleItemDetails;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private WatchItemDetails watchItemDetails;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private OtherItemDetails otherItemDetails;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
