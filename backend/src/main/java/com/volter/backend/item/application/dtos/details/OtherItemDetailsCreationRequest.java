package com.volter.backend.item.application.dtos.details;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherItemDetailsCreationRequest {
    private String category;

    private String description;
}
