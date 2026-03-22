package com.volter.backend.item.application.dtos.details;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchItemDetailsCreationRequest {
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
