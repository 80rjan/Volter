package com.volter.platform.modules.shop.domain.model;

import com.volter.platform.modules.shop.domain.model.enums.StaffShopStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Assignment of a staff member (identity context, referenced by id) to a
 * {@link Shop}. Governs which shops a staff member may operate in.
 */
@Entity
@Table(
        schema = "public",
        name = "staff_shop",
        uniqueConstraints = @UniqueConstraint(name = "uk_staff_shop", columnNames = {"staff_id", "shop_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StaffShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Staff is required")
    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @NotNull(message = "Shop is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false, foreignKey = @ForeignKey(name = "fk_staff_shop_shop"))
    private Shop shop;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StaffShopStatus status;

    @CreationTimestamp
    @Column(name = "assigned_at", nullable = false)
    private OffsetDateTime assignedAt;

    @Column(name = "unassigned_at")
    private OffsetDateTime unassignedAt;

    public static StaffShop assign(Long staffId, Shop shop) {
        return StaffShop.builder()
                .staffId(staffId)
                .shop(shop)
                .status(StaffShopStatus.ACTIVE)
                .build();
    }

    public void unassign() {
        if (status == StaffShopStatus.INACTIVE) {
            return;
        }
        this.status = StaffShopStatus.INACTIVE;
        this.unassignedAt = OffsetDateTime.now();
    }

    public boolean isActive() {
        return status == StaffShopStatus.ACTIVE;
    }
}
