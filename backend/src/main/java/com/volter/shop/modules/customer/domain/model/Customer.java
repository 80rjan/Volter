package com.volter.shop.modules.customer.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * A person who pawns or buys items at the shop. Tenant-scoped (lives in the
 * shop schema). Identified within a shop by their national id.
 */
@Entity
@Table(
        name = "customer",
        uniqueConstraints = @UniqueConstraint(name = "uk_customer_national_id", columnNames = "national_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "National id is required")
    @Column(name = "national_id", nullable = false, length = 32)
    private String nationalId;

    @NotBlank(message = "Primary phone is required")
    @Column(name = "phone_primary", nullable = false, length = 32)
    private String phonePrimary;

    @Column(name = "phone_secondary", length = 32)
    private String phoneSecondary;

    @NotBlank(message = "Address is required")
    @Column(name = "address", nullable = false)
    private String address;

    @NotBlank(message = "City is required")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public void updateContactDetails(String phonePrimary, String phoneSecondary, String address, String city) {
        this.phonePrimary = phonePrimary;
        this.phoneSecondary = phoneSecondary;
        this.address = address;
        this.city = city;
    }

    public void rename(String fullName) {
        this.fullName = fullName;
    }
}
