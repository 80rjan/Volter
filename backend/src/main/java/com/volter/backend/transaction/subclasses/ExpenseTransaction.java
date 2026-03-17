package com.volter.backend.transaction;

import com.volter.backend.expense.Expense;
import com.volter.backend.transaction.enums.TransactionAction;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("EXPENSE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExpenseTransaction extends Transaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_expense"))
    private Expense expense;

    @Override
    public String getCategory() {
        return "EXPENSE";
    }

    @Override
    protected void validate() {
        if (getAction() != TransactionAction.PAYMENT &&
                getAction() != TransactionAction.MODIFICATION) {
            throw new IllegalStateException(
                    "Invalid action for expense transaction: " + getAction()
            );
        }
    }
}