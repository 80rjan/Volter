package com.volter.shop.modules.transaction.application.dto.response.categories;

import com.volter.shop.modules.sale.application.dto.response.SaleDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.response.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class TransactionDetailedSaleResponse extends TransactionResponse implements TransactionDetailedResponse {

    @NotNull(message = "Sale response data is required")
    @Valid
    private SaleDetailedResponse sale;

    @Override
    public TransactionCategory getTransactionCategory() {
        return TransactionCategory.SALE;
    }
}
