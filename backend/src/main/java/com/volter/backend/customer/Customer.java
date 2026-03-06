package com.volter.backend.customer;

import com.volter.backend.customer.enums.CustomerRiskLevel;
import com.volter.backend.pawn.Pawn;
import com.volter.backend.sale.Sale;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_customer_embg", columnList = "embg"),
                @Index(name = "idx_customer_phone_number", columnList = "phoneNumber, reservePhoneNumber"),
                @Index(name = "idx_customer_risk_level", columnList = "riskLevel")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_embg", columnNames = "embg"),
                @UniqueConstraint(name = "uk_customer_phone_number", columnNames = "phoneNumber"),
                @UniqueConstraint(name = "uk_customer_reserve_phone_number", columnNames = "reservePhoneNumber")
        }
)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Customer name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Customer phone number is required")
    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = true)
    private String reservePhoneNumber;

    @NotNull(message = "Customer EMBG is required")
    @Column(columnDefinition = "CHAR(13)", nullable = false)
    private String embg;

    @NotNull(message = "Customer address is required")
    @Column(nullable = false)
    private String address;

    @NotNull(message = "Customer city is required")
    @Column(nullable = false)
    private String city;

    @NotNull(message = "Customer creation timestamp is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Customer update timestamp is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CustomerRiskLevel riskLevel;

    @OneToMany(mappedBy = "customer", cascade = {CascadeType.PERSIST}, orphanRemoval = false)
    private List<Pawn> pawns;

    @OneToMany(mappedBy = "customer", cascade = {CascadeType.PERSIST}, orphanRemoval = false)
    private List<Sale> sales;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
