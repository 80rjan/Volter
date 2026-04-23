package com.volter.shop.modules.expense.application.dto.request;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;
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
