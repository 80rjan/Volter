package com.volter.backend.expense.application.dto;

import com.volter.backend.expense.domain.model.enums.ExpenseType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseFilterDTO {

    private ExpenseType expenseType;

    private LocalDate fromDate;
    private LocalDate toDate;

}
