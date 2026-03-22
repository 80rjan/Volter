package com.volter.backend.item.application.dtos.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchItemDetailsResponse {

    private String brand;

    private String model;

    private String material;

    private Integer year;

    private boolean originalBoxIncluded;

    private boolean originalPapersIncluded;

    private boolean warrantyCardIncluded;

    private LocalDate warrantyExpirationDate;

    private boolean functional;

    private boolean serviceRequired;
}
