package com.volter.backend.item.application.dtos.details;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleItemDetailsCreationRequest {

    private String brand;

    private String model;

    private String vehicleType;

    private Integer year;

    private String registrationNumber;

    private Integer mileage;

    private Boolean serviceHistoryAvailable;

    private LocalDate lastServiceDate;

    private LocalDate registrationExpiryDate;

    private Integer numberOfKeys;
}
