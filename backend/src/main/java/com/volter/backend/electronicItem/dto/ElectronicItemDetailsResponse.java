package com.volter.backend.electronicItem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElectronicItemDetailsResponse {
    private String brand;
    private String category;
    private Integer year;
}
