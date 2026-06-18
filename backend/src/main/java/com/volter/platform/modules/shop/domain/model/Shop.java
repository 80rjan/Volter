package com.volter.platform.modules.shop.domain.model;

import com.volter.platform.modules.shop.domain.model.enums.ShopStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * Tenant registry entry. Each shop owns its own PostgreSQL schema
 * ({@code schemaName}); the platform/identity data lives in the public schema.
 */
@Entity
@Table(
        schema = "public",
        name = "shop",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_shop_code", columnNames = "code"),
                @UniqueConstraint(name = "uk_shop_schema_name", columnNames = "schema_name")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Shop name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Shop code is required")
    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @NotBlank(message = "Shop schema name is required")
    @Column(name = "schema_name", nullable = false, length = 63)
    private String schemaName;

    @Column(name = "address")
    private String address;

    @Column(name = "phone", length = 32)
    private String phone;

    @NotNull(message = "Shop status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ShopStatus status = ShopStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public boolean isActive() {
        return status == ShopStatus.ACTIVE;
    }

    public void close() {
        this.status = ShopStatus.CLOSED;
    }

    public void reopen() {
        this.status = ShopStatus.ACTIVE;
    }

    public void updateContactDetails(String address, String phone) {
        this.address = address;
        this.phone = phone;
    }
}
