package com.volter.shop.modules.cashregister.infrastructure.mapper;

import com.volter.shop.modules.cashregister.application.dto.CashRegisterResponse;
import com.volter.shop.modules.cashregister.application.dto.CashRegisterSessionResponse;
import com.volter.shop.modules.cashregister.application.dto.CashRegisterTransactionResponse;
import com.volter.shop.modules.cashregister.application.dto.DiscrepancyResponse;
import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSessionDiscrepancy;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CashRegisterMapper {

    CashRegisterResponse toResponse(CashRegister cashRegister);

    @Mapping(target = "cashRegisterId", source = "cashRegister.id")
    @Mapping(target = "cashRegisterCode", source = "cashRegister.code")
    @Mapping(target = "expectedInterest", expression = "java(session.getExpectedInterest() == null ? null : session.getExpectedInterest().amount())")
    CashRegisterSessionResponse toResponse(CashRegisterSession session);

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "sessionId", source = "transaction.cashRegisterSession.id")
    @Mapping(target = "staffId", source = "transaction.staffId")
    @Mapping(target = "amount", expression = "java(tx.getTransaction().getAmount() == null ? null : tx.getTransaction().getAmount().amount())")
    @Mapping(target = "direction", source = "transaction.direction")
    @Mapping(target = "description", source = "transaction.description")
    @Mapping(target = "createdAt", source = "transaction.createdAt")
    CashRegisterTransactionResponse toResponse(CashRegisterTransaction tx);

    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "staffId", source = "session.staffId")
    DiscrepancyResponse toResponse(CashRegisterSessionDiscrepancy discrepancy);
}
