package com.volter.backend.pawn.mapper;

import com.volter.backend.customer.mapper.CustomerMapper;
import com.volter.backend.item.mapper.ItemMapper;
import com.volter.backend.pawn.Pawn;
import com.volter.backend.pawn.dto.PawnDetailsResponse;
import com.volter.backend.pawn.dto.PawnResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, CustomerMapper.class})
public abstract class PawnMapper {
    public abstract PawnResponse toResponse(Pawn pawn);

    public abstract PawnDetailsResponse toDetailsResponse(Pawn pawn);

    @AfterMapping
    protected void enrichResponse(
            @MappingTarget PawnResponse pawnResponse,
            Pawn pawn
    ) {
        if (pawn.getCustomer() != null) {
            pawnResponse.setCustomerId(pawn.getCustomer().getId());
            pawnResponse.setCustomerName(pawn.getCustomer().getName());
        }
    }
}
