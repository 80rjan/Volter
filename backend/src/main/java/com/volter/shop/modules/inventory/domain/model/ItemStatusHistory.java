package com.volter.shop.modules.inventory.domain.model;

import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Append-only record of a single {@link Item} status transition, including who
 * performed it (identity context, by id). {@code fromStatus} is null for the
 * item's initial status.
 */
@Entity
@Table(name = "item_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ItemStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Item is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_status_history_item"))
    private Item item;

    @NotNull(message = "Staff is required")
    @Column(name = "changed_by_staff_id", nullable = false)
    private Long changedByStaffId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private ItemStatus fromStatus;

    @NotNull(message = "Target status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private ItemStatus toStatus;

    @CreationTimestamp
    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    static ItemStatusHistory of(Item item, Long staffId, ItemStatus fromStatus, ItemStatus toStatus) {
        return ItemStatusHistory.builder()
                .item(item)
                .changedByStaffId(staffId)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .occurredAt(OffsetDateTime.now())
                .build();
    }

    public boolean isInitial() {
        return fromStatus == null;
    }
}
