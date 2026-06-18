package com.volter.shop.modules.transaction.infrastructure.mapper;

import com.volter.shop.modules.transaction.application.dto.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "cashRegisterSessionId", source = "cashRegisterSession.id")
    @Mapping(target = "amount", expression = "java(transaction.getAmount() == null ? null : transaction.getAmount().amount())")
    TransactionResponse toResponse(Transaction transaction);
}
