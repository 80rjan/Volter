package com.volter.shop.modules.transaction.application.dto.response.categories;

import com.volter.shop.modules.transaction.application.dto.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.response.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;

public class TransactionDetailedCashRegisterResponse extends TransactionResponse implements TransactionDetailedResponse {



    @Override
    public TransactionCategory getTransactionCategory() {
        return TransactionCategory.CASH_REGISTER;
    }
}
