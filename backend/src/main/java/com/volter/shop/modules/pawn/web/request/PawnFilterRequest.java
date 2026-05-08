package com.volter.shop.modules.pawn.web.request;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.pawn.domain.model.enums.PawnStatus;

import java.time.LocalDate;

public record PawnFilterRequest(
        PawnStatus status,
        Boolean active,
        LocalDate fromMaturityDate,
        LocalDate toMaturityDate,
        LocalDate fromIssueDate,
        LocalDate toIssueDate,

        ItemType itemType,

        String customerName,
        String customerEmbg,
        String customerPhoneNumber
) {
}
