package com.volter.shop.modules.expense.web.request;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseFilterRequest {

    private ExpenseType expenseType;

    private LocalDate fromDate;
    private LocalDate toDate;

}
