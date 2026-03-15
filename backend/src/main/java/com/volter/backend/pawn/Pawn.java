package com.volter.backend.pawn;

import com.volter.backend.customer.Customer;
import com.volter.backend.customer.enums.CustomerRiskLevel;
import com.volter.backend.item.Item;
import com.volter.backend.pawn.enums.PawnStatus;
import com.volter.backend.pawnEvent.PawnEvent;
import com.volter.backend.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
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
                @Index(name = "idx_pawn_status", columnList = "status"),

                @Index(name = "idx_pawn_active_maturity_date", columnList = "active, maturityDate"),
                @Index(name = "idx_pawn_active_issue_date_desc", columnList = "active, issueDate DESC"),
                @Index(name = "idx_pawn_status_maturity_date", columnList = "status, maturityDate"),
                @Index(name = "idx_pawn_customer_id_active", columnList = "customer_id, active")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pawn_item_active", columnNames = {"item_id", "active"})     // one active pawn per item
        }
)
public class Pawn {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Pawn amount is required")
    @Column(nullable = false)
    private Integer amount;

    @NotNull(message = "Pawn interest is required")
    @Column(nullable = false)
    private Integer interest;

    @NotNull(message = "Pawn issue date is required")
    @Column(nullable = false)
    private LocalDate issueDate;

    @NotNull(message = "Pawn maturity date is required")
    @Column(nullable = false)
    private LocalDate maturityDate;

    @NotNull(message = "Pawn duration in days is required")
    @Min(1)
    @Column(nullable = false)
    private Integer durationDays;

    @NotNull(message = "Pawn status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PawnStatus status = PawnStatus.ACTIVE;

    @NotNull(message = "Pawn active status is required")
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @NotNull(message = "Pawn creation timestamp is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Pawn update timestamp is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @NotNull(message = "Pawn customer is required")
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_customer"))     // Customer.id
    private Customer customer;

    @NotNull(message = "Pawn item is required")
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_item"))     // Item.id
    private Item item;

    @Builder.Default
    @OneToMany(mappedBy = "pawn", cascade = {}, orphanRemoval = false)
    private List<Transaction> transactions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pawn", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<PawnEvent> pawnEvents = new ArrayList<>();

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
