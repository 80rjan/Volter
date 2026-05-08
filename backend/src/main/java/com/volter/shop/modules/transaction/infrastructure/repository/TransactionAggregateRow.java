package com.volter.shop.modules.transaction.infrastructure.repository;

public interface TransactionAggregateRow {
    String getCategory();
    String getType();
    Integer getCount();
    Integer getRevenue();
    Integer getCashOut();
    Integer getTurnover();
    Integer getGrossProfit();
    Integer getExpenses();
    Integer getNetProfit();
}
