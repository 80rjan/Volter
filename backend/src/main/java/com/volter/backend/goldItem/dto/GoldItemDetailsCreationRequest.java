package com.volter.backend.goldItem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoldItemDetailsCreationRequest {
    private Float weightGrams;

    private Float pricePerGram;

    private String carats;

    private String pieceType;
}
