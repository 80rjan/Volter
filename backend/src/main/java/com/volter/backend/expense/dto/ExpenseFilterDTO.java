package com.volter.backend.expense.dto;

import com.volter.backend.expense.enums.ExpenseType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseFilterDTO {

    private ExpenseType expenseType;

    private LocalDate fromDate;
    private LocalDate toDate;

}
