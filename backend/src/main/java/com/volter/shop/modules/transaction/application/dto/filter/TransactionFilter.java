package com.volter.shop.modules.transaction.application.dto.filter;

import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import lombok.Data;

@Data
public class TransactionFilter {
    private TransactionCategory transactionCategory;
    private TransactionDirection direction;
    private TransactionMarginType marginType;

    private String customerName;
    private String customerEmbg;
}
