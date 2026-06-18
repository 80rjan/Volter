package com.volter.shop.modules.cashregister.domain.model;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Subtype link giving a base {@link Transaction} its cash-register meaning
 * (a manual withdrawal, deposit or adjustment). One-to-one with the transaction.
 */
@Entity
@Table(
        name = "cash_register_transaction",
        uniqueConstraints = @UniqueConstraint(name = "uk_cr_tx_transaction", columnNames = "transaction_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CashRegisterTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Transaction is required")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_cr_tx_transaction"))
    private Transaction transaction;

    @NotNull(message = "Action is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private CashRegisterTransactionAction action;

    public static CashRegisterTransaction record(Transaction transaction, CashRegisterTransactionAction action) {
        return CashRegisterTransaction.builder()
                .transaction(transaction)
                .action(action)
                .build();
    }
}
