package com.volter.backend.item.application.dtos.details;

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
