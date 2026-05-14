package com.volter.shop.modules.customer.domain.model;

import com.volter.shop.modules.customer.domain.model.enums.CustomerRiskLevel;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.sale.domain.model.Sale;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@ToString
//@Table(
//        indexes = {
//                @Index(name = "idx_customer_embg", columnList = "embg"),
//                @Index(name = "idx_customer_phone_number", columnList = "phoneNumber, reservePhoneNumber"),
//                @Index(name = "idx_customer_risk_level", columnList = "riskLevel")
//        },
//        uniqueConstraints = {
//                @UniqueConstraint(name = "uk_customer_embg", columnNames = "embg"),
//                @UniqueConstraint(name = "uk_customer_phone_number", columnNames = "phoneNumber"),
//                @UniqueConstraint(name = "uk_customer_reserve_phone_number", columnNames = "reservePhoneNumber")
//        }
//)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Customer name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Customer phone number is required")
    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = true)
    private String reservePhoneNumber;

    @NotBlank(message = "Customer EMBG is required")
    @Column(columnDefinition = "CHAR(13)", nullable = false)
    private String embg;

    @NotBlank(message = "Customer address is required")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "Customer city is required")
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
    private Integer lateRenewalCount = 0;       // could be lateEventsCount for all events not only renewal?

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

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();

        riskLevel = calculateRiskLevel();
    }

    // todo: check the logic here
    private CustomerRiskLevel calculateRiskLevel() {
        if (totalPawnCount == 0) return CustomerRiskLevel.LOW;

        double lateRate = (double) lateRenewalCount / totalPawnCount;
        double forfeitRate = (double) forfeitCount / totalPawnCount;

        // HIGH risk conditions
        if (forfeitRate > 0.6) return CustomerRiskLevel.HIGH;
        if (lateRate > 0.6 && avgDaysLate > 10) return CustomerRiskLevel.HIGH;

        // MEDIUM risk conditions
        if (forfeitRate > 0.2) return CustomerRiskLevel.MEDIUM;
        if (lateRate > 0.3 || avgDaysLate > 5) return CustomerRiskLevel.MEDIUM;

        return CustomerRiskLevel.LOW;
    }

    public void pawnAction(PawnTransactionAction pawnTransactionAction) {
        pawnAction(pawnTransactionAction, null);
    }

    public void pawnAction(PawnTransactionAction pawnTransactionAction, Long daysLate) {
        switch (pawnTransactionAction) {
            case CREATION ->
                totalPawnCount++;
            case RENEWAL -> {
                if (daysLate == null)
                    throw new IllegalArgumentException("Days late must be provided for renewal action");

                if (daysLate <= 0)
                    onTimeRenewalCount++;
                else {
                    lateRenewalCount++;
                    avgDaysLate = ((avgDaysLate * (lateRenewalCount - 1)) + daysLate) / lateRenewalCount;
                }
            }
            case REDEMPTION ->
                redeemCount++;
            case FORFEITURE ->
                forfeitCount++;
        }
    }

    public void saleAction() {
        totalSaleCount++;
    }

}
