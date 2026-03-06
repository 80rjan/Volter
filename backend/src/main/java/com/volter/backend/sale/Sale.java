package com.volter.backend.sale;

import com.volter.backend.customer.Customer;
import com.volter.backend.item.Item;
import com.volter.backend.pawn.enums.PawnStatus;
import com.volter.backend.sale.enums.SaleStatus;
import com.volter.backend.transaction.Transaction;
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
                @Index(name = "idx_sale_status", columnList = "status"),

                @Index(name = "idx_sale_active_created_at_desc", columnList = "active, createdAt DESC"),
                @Index(name = "idx_sale_status_created_at_desc", columnList = "status, createdAt DESC"),
                @Index(name = "idx_sale_customer_id_active", columnList = "customer_id, active")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_sale_item_active", columnNames = {"item_id", "active"})     // one active sale per item
        }
)
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Sale listed price is required")
    @Column(nullable = false)
    private Integer listedPrice;

    @Column(nullable = true)
    private Integer soldPrice;

    @NotNull(message = "Sale status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status;

    @NotNull(message = "Sale active flag is required")
    @Column(nullable = false)
    private boolean active;

    @NotNull(message = "Sale creation timestamp is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Sale update timestamp is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @NotNull(message = "Sale customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sale_customer"))     // Customer.id
    private Customer customer;

    @NotNull(message = "Sale item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sale_item"))     // Item.id
    private Item item;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Transaction> transactions;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        active = true;
        status = SaleStatus.LISTED;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
