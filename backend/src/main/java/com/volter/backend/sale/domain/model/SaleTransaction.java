package com.volter.backend.sale.domain.model;

import com.volter.backend.transaction.domain.model.Transaction;
import com.volter.backend.transaction.domain.model.enums.TransactionAction;
import com.volter.backend.transaction.domain.model.enums.TransactionCategory;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_sale"))
    private Sale sale;

    @Override
    public TransactionCategory getCategory() {
        return TransactionCategory.SALE;
    }

    @Override
    protected void validate() {
        if (getAction() != TransactionAction.PURCHASE &&
                getAction() != TransactionAction.SALE &&
                getAction() != TransactionAction.MODIFICATION) {
            throw new IllegalStateException(
                    "Invalid action for sale transaction: " + getAction()
            );
        }
    }
}