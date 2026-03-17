package com.volter.backend.transaction;

import com.volter.backend.cashRegister.CashRegister;
import com.volter.backend.notification.Notification;
import com.volter.backend.staff.Staff;
import com.volter.backend.transaction.enums.TransactionAction;
import com.volter.backend.transaction.enums.TransactionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_category", discriminatorType = DiscriminatorType.STRING)
@Table(
        name = "transaction",
        indexes = {
                @Index(name = "idx_transaction_category", columnList = "transaction_category"),
                @Index(name = "idx_transaction_action", columnList = "action"),
                @Index(name = "idx_transaction_category_action", columnList = "transaction_category, action"),
                @Index(name = "idx_transaction_created_at_desc", columnList = "createdAt DESC"),
                @Index(name = "idx_transaction_category_created_at", columnList = "transaction_category, createdAt DESC"),
                @Index(name = "idx_transaction_employee_created_at", columnList = "employee_id, createdAt DESC"),
                @Index(name = "idx_transaction_cash_register_created_at", columnList = "cash_register_id, createdAt DESC")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Transaction action is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionAction action;

    @NotNull(message = "Transaction cash in is required")
    @Column(nullable = false)
    private Integer cashIn;

    @NotNull(message = "Transaction cash out is required")
    @Column(nullable = false)
    private Integer cashOut;

    @NotNull(message = "Transaction profit is required")
    @Column(nullable = false)
    private Integer profit;

    @NotNull(message = "Transaction created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private String description;

    @ManyToOne
    @JoinColumn(name = "cash_register_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_cash_register"))
    private CashRegister cashRegister;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_employee"))
    private Staff staff;

    @Builder.Default
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Notification> notifications = new ArrayList<>();

    // TODO: add a anomaly notification so i send notifications to the manager if the transaction smells of anomaly in it (cash in smaller than amount + profit for pawn, etc...

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        validate();
    }

    @PreUpdate
    protected void onUpdate() {
        validate();
    }

    protected abstract void validate();

    @Transient
    public abstract TransactionCategory getCategory();
}