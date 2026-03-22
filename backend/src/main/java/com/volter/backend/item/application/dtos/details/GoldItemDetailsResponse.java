package com.volter.backend.item.application.dtos.details;

import com.volter.backend.item.domain.model.enums.details.GoldItemCarats;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoldItemDetailsResponse {
    private Float weightGrams;

    private Float pricePerGram;

    private GoldItemCarats carats;

    private String pieceType;
}
