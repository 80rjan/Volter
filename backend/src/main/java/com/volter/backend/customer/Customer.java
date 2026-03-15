package com.volter.backend.customer;

import com.volter.backend.customer.enums.CustomerRiskLevel;
import com.volter.backend.pawn.Pawn;
import com.volter.backend.sale.Sale;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private CustomerRiskLevel riskLevel = CustomerRiskLevel.LOW;

    @NotNull(message = "Customer total pawn count is required")
    @Column(nullable = false)
    @Builder.Default
    private Integer totalPawnCount = 0;

    @NotNull(message = "Customer total sale count is required")
    @Column(nullable = false)
    @Builder.Default
    private Integer totalSaleCount = 0;

    @NotNull(message = "Customer late renewal count is required")
    @Column(nullable = false)
    @Builder.Default
    private Integer lateRenewalCount = 0;

    @NotNull(message = "Customer average days late is required")
    @Column(nullable = false)
    @Builder.Default
    private Double avgDaysLate = 0.0;

    @NotNull(message = "Customer on-time renewal count is required")
    @Column(nullable = false)
    @Builder.Default
    private Integer onTimeRenewalCount = 0;

    @NotNull(message = "Customer forfeit count is required")
    @Column(nullable = false)
    @Builder.Default
    private Integer forfeitCount = 0;

    @NotNull(message = "Customer redeem count is required")
    @Column(nullable = false)
    @Builder.Default
    private Integer redeemCount = 0;

    @Builder.Default
    @OneToMany(mappedBy = "customer", cascade = {}, orphanRemoval = false)
    private List<Pawn> pawns = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "customer", cascade = {}, orphanRemoval = false)
    private List<Sale> sales = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    private CustomerRiskLevel calculateRiskLevel() {
        if (totalPawnCount == 0) return CustomerRiskLevel.LOW;

        double lateRate = (double) lateRenewalCount / totalPawnCount;
        double forfeitRate = (double) getForfeitCount() / totalPawnCount;

        // HIGH risk conditions
        if (forfeitRate > 0.6) return CustomerRiskLevel.HIGH;
        if (lateRate > 0.6 && avgDaysLate > 10) return CustomerRiskLevel.HIGH;

        // MEDIUM risk conditions
        if (forfeitRate > 0.2) return CustomerRiskLevel.MEDIUM;
        if (lateRate > 0.3 || avgDaysLate > 5) return CustomerRiskLevel.MEDIUM;

        return CustomerRiskLevel.LOW;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();

        riskLevel = calculateRiskLevel();
    }
}
