package com.volter.backend.pawn.domain.model;

import com.volter.backend.transaction.domain.model.Transaction;
import com.volter.backend.transaction.domain.model.enums.TransactionAction;
import com.volter.backend.transaction.domain.model.enums.TransactionCategory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("PAWN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PawnTransaction extends Transaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pawn_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_pawn"))
    private Pawn pawn;

    @Override
    public TransactionCategory getCategory() {
        return TransactionCategory.PAWN;
    }

    @Override
    protected void validate() {
        if (getAction() != TransactionAction.CREATION &&
                getAction() != TransactionAction.RENEWAL &&
                getAction() != TransactionAction.REDEMPTION &&
                getAction() != TransactionAction.FORFEITURE &&
                getAction() != TransactionAction.MODIFICATION) {
            throw new IllegalStateException(
                    "Invalid action for pawn transaction: " + getAction()
            );
        }
    }
}