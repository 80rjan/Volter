package com.volter.backend.electronicItem.dto;

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
