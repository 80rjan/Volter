package com.volter.backend.transaction.subclasses;

import com.volter.backend.transaction.Transaction;
import com.volter.backend.transaction.enums.TransactionAction;
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
    public String getCategory() {
        return "CASH_REGISTER";
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
