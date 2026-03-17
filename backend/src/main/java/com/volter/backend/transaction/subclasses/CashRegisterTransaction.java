package com.volter.backend.transaction.subclasses;

import com.volter.backend.transaction.Transaction;
import com.volter.backend.transaction.enums.TransactionAction;
import com.volter.backend.transaction.enums.TransactionCategory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("CASH_REGISTER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CashRegisterTransaction extends Transaction {

    @Override
    public TransactionCategory getCategory() {
        return TransactionCategory.CASH_REGISTER;
    }

    protected void validate() {
        if (getAction() != TransactionAction.DEPOSIT &&
                getAction() != TransactionAction.WITHDRAW) {
            throw new IllegalStateException(
                    "Invalid action for cash register operation: " + getAction()
            );
        }
    }
}
