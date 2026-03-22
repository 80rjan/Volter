package com.volter.backend.transaction.application.dto;

import com.volter.backend.transaction.domain.model.enums.TransactionCategory;
import lombok.Data;

@Data
public class TransactionFilterDTO {

    private String customerName;

    private String customerEmbg;

    private TransactionCategory transactionCategory;
}
