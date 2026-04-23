package com.volter.shop.modules.pawn.infrastructure;

import com.volter.shop.modules.customer.infrastructure.CustomerMapper;
import com.volter.shop.modules.inventory.infrastructure.ItemDetailedMapper;
import com.volter.shop.modules.inventory.infrastructure.ItemMapper;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.pawn.application.dto.response.PawnDetailedResponse;
import com.volter.shop.modules.pawn.application.dto.response.PawnResponse;
import com.volter.shop.shared.valueobject.Money;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, ItemDetailedMapper.class, CustomerMapper.class})
public abstract class PawnMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")

    @Mapping(target = "issueDate", expression = "java(pawn.getPeriod().issueDate())")
    @Mapping(target = "maturityDate", expression = "java(pawn.getPeriod().maturityDate())")

    @Mapping(target = "item", source = "item", qualifiedByName = "toBaseResponse")
    public abstract PawnResponse toResponse(Pawn pawn);
    public abstract List<PawnResponse> toResponse(List<Pawn> pawns);

    @InheritConfiguration(name = "toResponse")

    @Mapping(target = "issueDate", expression = "java(pawn.getPeriod().issueDate())")
    @Mapping(target = "maturityDate", expression = "java(pawn.getPeriod().maturityDate())")

    @Mapping(target = "item", source = "item", qualifiedByName = "toDetailedResponse")
    public abstract PawnDetailedResponse toDetailedResponse(Pawn pawn);
    public abstract List<PawnDetailedResponse> toDetailedResponse(List<Pawn> pawns);


    protected Integer map(Money money) {
        return money != null ? money.amount() : null;
    }

}
