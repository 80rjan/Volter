package com.volter.backend.item.application.dtos.details;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectronicItemDetailsCreationRequest {
    private String brand;

    private String category;

    private Integer year;
}
