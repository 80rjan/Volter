package com.volter.backend.otherItem.dto;

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
