package com.volter.identity.modules.staff.domain.model;

import com.volter.identity.modules.role.domain.model.Role;
import com.volter.identity.modules.staff.domain.model.enums.StaffRoleStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Assignment of a {@link Role} to a {@link Staff} member, scoped to a single
 * shop. The same staff member can hold different roles in different shops.
 * The owning shop is referenced by id ({@code shopId}) because Shop lives in
 * the platform bounded context.
 */
@Entity
@Table(
        schema = "public",
        name = "staff_role"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StaffRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false, foreignKey = @ForeignKey(name = "fk_staff_role_staff"))
    private Staff staff;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_staff_role_role"))
    private Role role;

    @NotNull(message = "Owning shop is required")
    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StaffRoleStatus status;

    @CreationTimestamp
    @Column(name = "granted_at", nullable = false)
    private OffsetDateTime grantedAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    public static StaffRole grant(Staff staff, Role role, Long shopId) {
        return StaffRole.builder()
                .staff(staff)
                .role(role)
                .shopId(shopId)
                .status(StaffRoleStatus.GRANTED)
                .build();
    }

    public void revoke() {
        if (status == StaffRoleStatus.REVOKED) {
            return;
        }
        this.status = StaffRoleStatus.REVOKED;
        this.revokedAt = OffsetDateTime.now();
    }

    public boolean isGranted() {
        return status == StaffRoleStatus.GRANTED;
    }

    public boolean appliesTo(Long candidateShopId) {
        return shopId.equals(candidateShopId);
    }
}
