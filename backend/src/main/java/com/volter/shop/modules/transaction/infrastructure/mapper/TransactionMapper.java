package com.volter.shop.modules.transaction.infrastructure.mapper;

import com.volter.shop.modules.transaction.application.dto.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.ActivityEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // `id` is the id within the row's own source, so a PAWN_EVENT row reports its
    // event id and a TRANSACTION row its transaction id; `entryId` stays unique
    // across the merged list.
    @Mapping(target = "id", source = "entry.sourceId")
    @Mapping(target = "clientName", source = "clientName")
    TransactionResponse toResponse(ActivityEntry entry, String clientName);
}
