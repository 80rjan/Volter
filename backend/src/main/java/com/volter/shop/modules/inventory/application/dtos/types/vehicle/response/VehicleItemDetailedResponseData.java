package com.volter.shop.modules.inventory.application.dtos.types.vehicle.response;

import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemDetailedResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.types.VehicleItemVehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleItemDetailedResponseData extends VehicleItemResponseData
        implements ItemDetailedResponseData {

    @NotNull(message = "Item status is required")
    private ItemStatus itemStatus;

    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    @NotNull(message = "Updated at timestamp is required")
    private LocalDateTime updatedAt;


    @NotBlank(message = "Vehicle type is required")
    private VehicleItemVehicleType vehicleType;

    @NotNull(message = "Mileage is required")
    private Integer mileage;

    private boolean serviceHistoryAvailable;

    // nullable
    private LocalDate lastServiceDate;

    @NotNull(message = "Registration expiry date is required")
    private LocalDate registrationExpiryDate;

    @NotNull(message = "Number of keys is required")
    private Integer numberOfKeys;
}
