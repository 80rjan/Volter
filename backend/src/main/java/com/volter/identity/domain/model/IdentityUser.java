package com.volter.identity.domain.model;

import com.volter.identity.domain.model.enums.PermissionEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "public")
public class IdentityUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "IdentityUser name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "IdentityUser username is required")
    @Column(nullable = false, unique = true)    // todo: unique index in @Table
    private String username;

    @NotNull(message = "IdentityUser password hash is required")
    @Column(nullable = false)
    private String passwordHash;

    @NotNull(message = "IdentityUser embg is required")
    @Column(columnDefinition = "CHAR(13)", nullable = false, unique = true)    // todo: unique index in @Table
    private String embg;

    @NotNull(message = "IdentityUser phone number is required")
    @Column(nullable = false)
    private String phoneNumber;

    @NotNull(message = "IdentityUser reserve phone number is required")
    @Column(nullable = false)
    private String reservePhoneNumber;

    @NotNull(message = "Staff created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Staff updated at is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @NotNull(message = "Staff is deleted is required")
    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_role"))
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_assigned_shop",
            schema = "public",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_assigned_shop_user")),
            inverseJoinColumns = @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "fk_user_assigned_shop_shop"))
    )
    private List<Shop> assignedShops;

    @Setter
    @Getter
    @Transient  // for duration of request
    private String currentShopSchema;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        deleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean canDo(PermissionEnum requiredPermission) {
        return role.getPermissions()    // this ok bcs role.getPermissions is EAGER
                .stream()
                .anyMatch(p -> p.getName() == requiredPermission);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            role.getPermissions().forEach(p ->
                    authorities.add(new SimpleGrantedAuthority(p.getName().getPermission()))
            );
        }
        return authorities;
    }

    @Override
    public String getPassword() { return passwordHash; }

    @Override
    public String getUsername() { return id.toString(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return !isDeleted(); }
}
