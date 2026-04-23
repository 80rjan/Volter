package com.volter.shop.modules.inventory.domain.model.types;

import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.enums.types.VehicleItemVehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@DiscriminatorValue("VEHICLE")
public class VehicleItem extends Item{

    @NotBlank(message = "Vehicle brand is required")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Vehicle model is required")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Vehicle type is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleItemVehicleType vehicleType;

    @NotNull(message = "Vehicle year is required")
    @Column(nullable = false)
    private Integer year;

    @NotBlank(message = "Vehicle registration number is required")
    @Column(nullable = false)
    private String registrationNumber;

    @NotNull(message = "Vehicle mileage is required")
    @Column(nullable = false)
    private Integer mileage;

    @NotNull(message = "Vehicle is service history available is required")
    @Column(nullable = false)
    private boolean serviceHistoryAvailable;

    @Column(nullable = true)
    private LocalDate lastServiceDate;

    @NotNull(message = "Vehicle registration expiry date is required")
    @Column(nullable = false)
    private LocalDate registrationExpiryDate;

    @NotNull(message = "Vehicle number of keys is required")
    @Column(nullable = false)
    private Integer numberOfKeys;
}
