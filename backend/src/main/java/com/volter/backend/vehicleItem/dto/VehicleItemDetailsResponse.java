package com.volter.backend.vehicleItem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleItemDetailsResponse {
    private String brand;

    private String model;

    private String vehicleType;

    private Integer year;

    private String registrationNumber;

    private Integer mileage;

    private boolean serviceHistoryAvailable;

    private LocalDate lastServiceDate;

    private LocalDate registrationExpiryDate;

    private Integer numberOfKeys;
}
