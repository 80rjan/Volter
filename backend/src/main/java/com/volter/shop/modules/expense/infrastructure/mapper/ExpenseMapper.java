package com.volter.shop.modules.expense.infrastructure.mapper;

import com.volter.shop.modules.expense.application.dto.ExpenseResponse;
import com.volter.shop.modules.expense.domain.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "amount", expression = "java(expense.getAmount() == null ? null : expense.getAmount().amount())")
    @Mapping(target = "staffName", source = "staffName")
    ExpenseResponse toResponse(Expense expense, String staffName);
}
