package com.volter.shop.modules.transaction.web.response.categories;

import com.volter.shop.modules.transaction.web.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.web.response.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;

public class TransactionDetailedCashRegisterResponse extends TransactionResponse implements TransactionDetailedResponse {



    @Override
    public TransactionCategory getTransactionCategory() {
        return TransactionCategory.CASH_REGISTER;
    }
}
