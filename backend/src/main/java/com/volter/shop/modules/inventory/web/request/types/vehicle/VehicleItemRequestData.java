package com.volter.shop.modules.inventory.web.request.types.vehicle;

import com.volter.shop.modules.inventory.web.request.baseitem.ItemRequestData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.inventory.domain.model.enums.types.VehicleItemVehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleItemRequestData implements ItemRequestData {

    @NotNull(message = "Item origin type is required")
    private ItemOriginType itemOriginType;

    @NotNull(message = "Item status is required")
    private ItemStatus itemStatus;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Vehicle type is required")
    private VehicleItemVehicleType vehicleType;

    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private Integer year;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotNull(message = "Mileage is required")
    @Positive(message = "Mileage must be positive")
    private Integer mileage;

    @NotNull(message = "Service history available flag is required")
    private Boolean serviceHistoryAvailable;

    private LocalDate lastServiceDate;

    @NotNull(message = "Registration expiry date is required")
    private LocalDate registrationExpiryDate;

    @NotNull(message = "Number of keys is required")
    @Positive(message = "Number of keys must be positive")
    private Integer numberOfKeys;

    @Override
    public ItemType getItemType() {
        return ItemType.VEHICLE;
    }
}