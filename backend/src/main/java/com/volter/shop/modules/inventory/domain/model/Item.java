package com.volter.shop.modules.inventory.domain.model;

import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * A physical item handled by the shop. A single table holds every item type;
 * the type-specific data (carats, weight, brand, plate, ...) lives in the
 * flexible {@code attributes} JSON column, keyed by {@link #type}.
 */
@Entity
@Table(name = "item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Item type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ItemType type;

    @NotNull(message = "Item origin is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false, length = 50)
    private ItemOriginType origin;

    @NotNull(message = "Item status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ItemStatus status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // ----- attributes -----

    public Object attribute(String key) {
        return attributes == null ? null : attributes.get(key);
    }

    public void putAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
    }

    public void describe(String description) {
        this.description = description;
    }

    // ----- status transitions -----

    /**
     * Moves the item to {@code newStatus} and returns the history record for the
     * change. The caller (service) is responsible for persisting the history.
     */
    public ItemStatusHistory changeStatus(ItemStatus newStatus, Long staffId) {
        ItemStatus previous = this.status;
        this.status = newStatus;
        return ItemStatusHistory.of(this, staffId, previous, newStatus);
    }

    public ItemStatusHistory markInPawn(Long staffId) {
        return changeStatus(ItemStatus.IN_PAWN, staffId);
    }

    public ItemStatusHistory markRedeemed(Long staffId) {
        return changeStatus(ItemStatus.REDEEMED, staffId);
    }

    public ItemStatusHistory markInSale(Long staffId) {
        return changeStatus(ItemStatus.IN_SALE, staffId);
    }

    public ItemStatusHistory markSold(Long staffId) {
        return changeStatus(ItemStatus.SOLD, staffId);
    }

    public boolean isAvailableForPawn() {
        return status == ItemStatus.REDEEMED || status == ItemStatus.IN_SALE;
    }
}
