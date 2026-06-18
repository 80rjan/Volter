package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PawnFilterRequest(
        String customerFullName,
        String customerNationalId,
        String customerPhone,
        ItemType itemType,
        PawnContractStatus status,
        LocalDate issuedFrom,
        LocalDate issuedTo,
        LocalDate dueFrom,
        LocalDate dueTo,
        Boolean overdueOnly
) {
}
