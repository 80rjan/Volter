package com.volter.shop.modules.transaction.domain.model;

import com.volter.shop.modules.alert.domain.RiskAlert;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import com.volter.shop.shared.valueobject.Money;
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
//@Table(
//        name = "transaction",
//        indexes = {
//                @Index(name = "idx_transaction_category", columnList = "transaction_category"),
//                @Index(name = "idx_transaction_action", columnList = "action"),
//                @Index(name = "idx_transaction_category_action", columnList = "transaction_category, action"),
//                @Index(name = "idx_transaction_created_at_desc", columnList = "createdAt DESC"),
//                @Index(name = "idx_transaction_category_created_at", columnList = "transaction_category, createdAt DESC"),
//                @Index(name = "idx_transaction_employee_created_at", columnList = "employee_id, createdAt DESC"),
//                @Index(name = "idx_transaction_cash_register_created_at", columnList = "cash_register_id, createdAt DESC")
//        }
//)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_category", insertable = false, updatable = false, nullable = false)
    private TransactionCategory transactionCategory;

    @Embedded
    @NotNull(message = "Transaction amount is required")
    @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false))
    private Money amount;

    @NotNull(message = "Transaction direction is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionDirection direction;

    @Embedded
    @NotNull(message = "Transaction margin amount is required")
    @AttributeOverride(name = "amount", column = @Column(name = "margin_amount", nullable = false))
    private Money marginAmount; // of which profit

    @NotNull(message = "Transaction margin type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionMarginType marginType;

    @NotNull(message = "Transaction created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private String description; // could be VO??

    @Builder.Default
    @OneToMany(mappedBy = "transaction", orphanRemoval = false)
    private List<RiskAlert> riskAlerts = new ArrayList<>();

    @NotNull(message = "Transaction staff is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_employee"))
    private Staff staff;

    @NotNull(message = "Transaction cash register session is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_register_session_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_cash_register_session"))
    private CashRegisterSession cashRegisterSession;

    // TODO: add a anomaly risk alert so i send riskAlerts to the manager if the transaction smells of anomaly in it (cash in smaller than amount + profit for pawn, etc...

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Transient
    public abstract TransactionCategory getCategory();
}