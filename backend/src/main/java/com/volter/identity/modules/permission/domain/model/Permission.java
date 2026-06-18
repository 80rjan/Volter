package com.volter.identity.modules.permission.domain.model;

import com.volter.identity.modules.permission.domain.model.enums.PermissionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * A single, atomic capability that can be granted to a {@link Role}.
 * Part of the dynamic IAM model in the public schema: permission names are
 * data (rows), not a fixed compile-time enum.
 */
@Entity
@Table(schema = "public", name = "permission")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Permission name is required")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private PermissionCategory category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public boolean hasName(String permissionName) {
        return name.equals(permissionName);
    }

    public boolean isInCategory(PermissionCategory candidate) {
        return category == candidate;
    }
}
