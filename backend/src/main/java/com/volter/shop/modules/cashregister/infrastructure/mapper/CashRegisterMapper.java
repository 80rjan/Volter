package com.volter.shop.modules.cashregister.infrastructure.mapper;

import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.web.response.CashRegisterResponse;
import com.volter.shop.modules.cashregister.web.response.CashRegisterSessionResponse;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.shared.valueobject.Money;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CashRegisterMapper {

    public abstract CashRegisterResponse toResponse(CashRegister cashRegister);

    public abstract CashRegisterSessionResponse toSessionResponse(CashRegisterSession cashRegisterSession);
    public abstract List<CashRegisterSessionResponse> toSessionResponse(List<CashRegisterSession> cashRegisterSessions);

    protected Integer map(Money money) {
        return money != null ? money.amount() : null;
    }
}
