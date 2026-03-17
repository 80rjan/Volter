package com.volter.backend.transaction.dto;

import com.volter.backend.transaction.enums.TransactionCategory;
import lombok.Data;

@Data
public class TransactionFilterDTO {

    private String customerName;

    private String customerEmbg;

    private TransactionCategory transactionCategory;
}
