package com.volter.backend.expense.dto;

import com.volter.backend.expense.enums.ExpenseType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseCreationRequest {

    private ExpenseType expenseType;

    private Integer amount;

    private String description;

    private LocalDate date;

}
