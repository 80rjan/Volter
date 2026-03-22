package com.volter.backend.pawn.infrastructure;

import com.volter.backend.customer.infrastructure.CustomerMapper;
import com.volter.backend.item.infrastructure.ItemMapper;
import com.volter.backend.pawn.domain.model.Pawn;
import com.volter.backend.pawn.application.dto.PawnCreationRequest;
import com.volter.backend.pawn.application.dto.PawnDetailsResponse;
import com.volter.backend.pawn.application.dto.PawnResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, CustomerMapper.class})
public abstract class PawnMapper {
    public abstract PawnResponse toResponse(Pawn pawn);

    public abstract PawnDetailsResponse toDetailsResponse(Pawn pawn);

    public abstract Pawn toEntity(PawnCreationRequest request);

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
