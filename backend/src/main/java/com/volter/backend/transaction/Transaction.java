package com.volter.backend.transaction;

import com.volter.backend.cashRegister.CashRegister;
import com.volter.backend.staff.Staff;
import com.volter.backend.modificationNotification.ModificationNotification;
import com.volter.backend.pawn.Pawn;
import com.volter.backend.sale.Sale;
import com.volter.backend.transaction.enums.TransactionType;
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
                @Index(name = "idx_transaction_pawn_id", columnList = "pawn_id, createdAt DESC"),
                @Index(name = "idx_transaction_sale_id", columnList = "sale_id, createdAt DESC"),
                @Index(name = "idx_transaction_created_at_desc", columnList = "createdAt DESC"),
                @Index(name = "idx_transaction_type_created_at_desc", columnList = "transactionType, createdAt DESC"),
                @Index(name = "idx_transaction_employee_created_at_desc", columnList = "employee_id, createdAt DESC"),
                @Index(name = "idx_transaction_cash_register_created_at_desc", columnList = "cash_register_id, createdAt DESC")
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pawn_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_pawn"))      // Pawn.id
    private Pawn pawn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_sale"))      // Sale.id
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "cash_register_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_cash_register"))      // CashRegister.id
    private CashRegister cashRegister;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_employee"))      // Staff.id
    private Staff staff;

    @Builder.Default
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<ModificationNotification> modificationNotifications = new ArrayList<>();

    // TODO: add a anomaly notification so i send notifications to the manager if the transaction smells of anomaly in it (cash in smaller than amount + profit for pawn, etc...

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
