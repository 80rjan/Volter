package com.volter.backend.expense.mapper;

import com.volter.backend.expense.Expense;
import com.volter.backend.expense.dto.ExpenseCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ExpenseMapper {

    public abstract Expense toEntity(ExpenseCreationRequest request);
}
