package com.volter.identity.modules.role.domain.model;

import com.volter.identity.modules.permission.domain.model.Permission;
import com.volter.identity.modules.staff.domain.model.StaffRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A named bundle of {@link Permission}s. Roles are assigned to staff per shop
 * through {@link StaffRole}. Dynamic IAM: role names are data, not an enum.
 */
@Entity
@Table(schema = "public", name = "role")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Role name is required")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            schema = "public",
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_role_permission_role")),
            inverseJoinColumns = @JoinColumn(name = "permission_id", foreignKey = @ForeignKey(name = "fk_role_permission_permission"))
    )
    private Set<Permission> permissions = new HashSet<>();

    public void grant(Permission permission) {
        permissions.add(permission);
    }

    public void revoke(Permission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(String permissionName) {
        return permissions.stream().anyMatch(p -> p.hasName(permissionName));
    }

    public boolean hasName(String roleName) {
        return name.equals(roleName);
    }

    public void rename(String newName) {
        this.name = newName;
    }
}
