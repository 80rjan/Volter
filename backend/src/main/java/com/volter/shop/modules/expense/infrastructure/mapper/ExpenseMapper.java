package com.volter.shop.modules.expense.infrastructure.mapper;

import com.volter.shop.modules.expense.web.response.ExpenseResponse;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.shared.valueobject.Money;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ExpenseMapper {

    public abstract ExpenseResponse toResponse(Expense expense);

     protected Integer map(Money money) {
        return money != null ? money.amount() : null;
    }
}
