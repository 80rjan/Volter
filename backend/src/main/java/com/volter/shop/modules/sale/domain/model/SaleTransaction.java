package com.volter.shop.modules.sale.domain.model;

import com.volter.shop.modules.sale.domain.model.enums.SaleTransactionAction;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SALE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SaleTransaction extends Transaction {

    @NotNull(message = "Transaction action is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleTransactionAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_sale"))
    private Sale sale;

    @Override
    public TransactionCategory getCategory() {
        return TransactionCategory.SALE;
    }
}