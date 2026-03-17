package com.volter.backend.transaction.subclasses;

import com.volter.backend.pawn.Pawn;
import com.volter.backend.transaction.Transaction;
import com.volter.backend.transaction.enums.TransactionAction;
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
    public String getCategory() {
        return "PAWN";
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