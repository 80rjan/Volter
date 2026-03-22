package com.volter.backend.cashRegister.domain.model;

import com.volter.backend.transaction.domain.model.Transaction;
import com.volter.backend.transaction.domain.model.enums.TransactionAction;
import com.volter.backend.transaction.domain.model.enums.TransactionCategory;
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
