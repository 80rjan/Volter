package com.volter.backend.goldItem.dto;

import com.volter.backend.goldItem.enums.GoldItemCarats;
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
