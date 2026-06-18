package com.volter.shop.modules.sale.domain.model;

import com.volter.shop.modules.sale.domain.model.enums.SaleTransactionAction;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Subtype link giving a base {@link Transaction} its sale meaning, tying it to a
 * {@link Sale} and the action that produced it (listing creation or the sale
 * itself). One-to-one with the transaction.
 */
@Entity
@Table(name = "sale_transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SaleTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Transaction is required")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_sale_tx_transaction"))
    private Transaction transaction;

    @NotNull(message = "Sale is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sale_tx_sale"))
    private Sale sale;

    @NotNull(message = "Action is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private SaleTransactionAction action;

    public static SaleTransaction record(Transaction transaction, Sale sale, SaleTransactionAction action) {
        return SaleTransaction.builder()
                .transaction(transaction)
                .sale(sale)
                .action(action)
                .build();
    }
}
