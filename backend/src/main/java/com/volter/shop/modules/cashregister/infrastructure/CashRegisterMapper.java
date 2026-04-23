package com.volter.shop.modules.cashregister.infrastructure;

import com.volter.shop.modules.cashregister.application.dto.response.CashRegisterSessionResponse;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.shared.valueobject.Money;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CashRegisterMapper {

    public abstract CashRegisterSessionResponse toSessionResponse(CashRegisterSession cashRegisterSession);
    public abstract List<CashRegisterSessionResponse> toSessionResponse(List<CashRegisterSession> cashRegisterSessions);

    protected Integer map(Money money) {
        return money != null ? money.amount() : null;
    }
}
