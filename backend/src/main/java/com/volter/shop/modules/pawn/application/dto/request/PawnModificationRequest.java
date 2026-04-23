package com.volter.shop.modules.pawn.application.dto.request;

import com.volter.shop.modules.inventory.application.dtos.baseitem.request.ItemModificationRequest;

public record PawnModificationRequest (
    Integer amount,
    Integer interest,
    Integer durationDays,
    ItemModificationRequest itemModificationRequest,

    String transactionDescription
) {}
