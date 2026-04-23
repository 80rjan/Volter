package com.volter.shop.modules.expense.application.dto.filter;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseFilter {

    private ExpenseType expenseType;

    private LocalDate fromDate;
    private LocalDate toDate;

}
