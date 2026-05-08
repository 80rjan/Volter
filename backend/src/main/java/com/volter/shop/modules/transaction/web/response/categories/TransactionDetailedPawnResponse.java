package com.volter.shop.modules.transaction.web.response.categories;

import com.volter.shop.modules.pawn.web.response.PawnDetailedResponse;
import com.volter.shop.modules.transaction.web.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.web.response.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class TransactionDetailedPawnResponse extends TransactionResponse
        implements TransactionDetailedResponse {

    @NotNull(message = "Pawn response data is required")
    @Valid
    PawnDetailedResponse pawn;


    @Override
    public TransactionCategory getTransactionCategory() {
        return TransactionCategory.PAWN;
    }
}
