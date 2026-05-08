package com.volter.shop.modules.inventory.domain.model;

import com.volter.shop.modules.inventory.web.request.baseitem.ItemModificationRequest;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.sale.domain.model.Sale;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Table(
        indexes = {
                @Index(name = "idx_item_item_type", columnList = "item_type"),
                @Index(name = "idx_item_item_origin_type", columnList = "item_origin_type"),
                @Index(name = "idx_item_item_status", columnList = "item_status"),
                @Index(name = "idx_item_created_at_desc", columnList = "created_at DESC")
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_type")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", insertable = false, updatable = false, nullable = false)
    private ItemType itemType;

    @NotNull(message = "Item origin type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemOriginType itemOriginType;

    @NotNull(message = "Item status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus itemStatus;

    @NotBlank(message = "Item description is required")
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
    @OneToMany(mappedBy = "item", cascade = {}, orphanRemoval = false)
    private List<Sale> sales = new ArrayList<>();

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

    public void modify(ItemModificationRequest request) {
        this.description = request.description();
    }
}
