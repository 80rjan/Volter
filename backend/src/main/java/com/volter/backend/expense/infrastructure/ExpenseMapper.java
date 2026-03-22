package com.volter.backend.expense.infrastructure;

import com.volter.backend.expense.domain.model.Expense;
import com.volter.backend.expense.application.dto.ExpenseCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ExpenseMapper {

    public abstract Expense toEntity(ExpenseCreationRequest request);
}
