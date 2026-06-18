package com.volter.identity.modules.staff.domain.model;

import com.volter.identity.modules.role.domain.model.Role;
import com.volter.identity.modules.staff.domain.model.enums.StaffRoleStatus;
import com.volter.identity.modules.staff.domain.model.enums.StaffStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The authentication principal and IAM subject. Lives in the public schema and
 * is shared across all tenant (shop) schemas. A staff member can be assigned to
 * many shops and hold different roles per shop via {@link StaffRole}.
 */
@Entity
@Table(
        schema = "public",
        name = "staff"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Username is required")
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @NotBlank(message = "Password hash is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "National id is required")
    @Column(name = "national_id", nullable = false, length = 32)
    private String nationalId;

    @NotBlank(message = "Primary phone is required")
    @Column(name = "phone_primary", nullable = false, length = 32)
    private String phonePrimary;

    @Column(name = "phone_secondary", length = 32)
    private String phoneSecondary;

    @NotNull(message = "Base salary is required")
    @Column(name = "base_salary", nullable = false)
    private Integer baseSalary;

    @NotNull(message = "Bonus percent is required")
    @Column(name = "bonus_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal bonusPercent;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private StaffStatus status = StaffStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", foreignKey = @ForeignKey(name = "fk_staff_manager"))
    private Staff manager;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Builder.Default
    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffRole> staffRoles = new ArrayList<>();

    // ----- compensation -----

    public Integer bonusAmount() {
        return BigDecimal.valueOf(baseSalary)
                .multiply(bonusPercent)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .intValue();
    }

    public Integer totalCompensation() {
        return baseSalary + bonusAmount();
    }

    // ----- lifecycle / status -----

    public boolean isActive() {
        return status == StaffStatus.ACTIVE && deletedAt == null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void activate() {
        this.status = StaffStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = StaffStatus.INACTIVE;
    }

    public void suspend() {
        this.status = StaffStatus.SUSPENDED;
    }

    public void softDelete() {
        if (deletedAt == null) {
            this.deletedAt = OffsetDateTime.now();
            this.status = StaffStatus.INACTIVE;
        }
    }

    // ----- profile -----

    public void updateProfile(String phonePrimary,
                              String phoneSecondary,
                              Integer baseSalary,
                              BigDecimal bonusPercent) {
        this.fullName = fullName;
        this.phonePrimary = phonePrimary;
        this.phoneSecondary = phoneSecondary;
        this.baseSalary = baseSalary;
        this.bonusPercent = bonusPercent;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void assignManager(Staff manager) {
        if (manager != null && manager.getId() != null && manager.getId().equals(this.id)) {
            throw new IllegalArgumentException("Staff cannot manage themselves");
        }
        this.manager = manager;
    }

    // ----- IAM -----

    public StaffRole assignRole(Role role, Long shopId) {
        staffRoles.stream()
                .filter(sr -> sr.getRole().equals(role) && sr.appliesTo(shopId) && sr.isGranted())
                .findFirst()
                .ifPresent(sr -> {
                    throw new IllegalStateException("Role already granted to staff for this shop");
                });

        StaffRole staffRole = StaffRole.grant(this, role, shopId);
        staffRoles.add(staffRole);
        return staffRole;
    }

    public void revokeRole(Role role, Long shopId) {
        staffRoles.stream()
                .filter(sr -> sr.getRole().equals(role) && sr.appliesTo(shopId) && sr.isGranted())
                .forEach(StaffRole::revoke);
    }

    public boolean hasRole(String roleName, Long shopId) {
        return staffRoles.stream()
                .filter(sr -> sr.isGranted() && sr.appliesTo(shopId))
                .anyMatch(sr -> sr.getRole().hasName(roleName));
    }

    public boolean hasPermission(String permissionName, Long shopId) {
        return staffRoles.stream()
                .filter(sr -> sr.isGranted() && sr.appliesTo(shopId))
                .anyMatch(sr -> sr.getRole().hasPermission(permissionName));
    }

    public boolean worksInShop(Long shopId) {
        return staffRoles.stream()
                .anyMatch(sr -> sr.getStatus() == StaffRoleStatus.GRANTED && sr.appliesTo(shopId));
    }
}
