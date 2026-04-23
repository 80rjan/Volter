package com.volter.shop.modules.inventory.application.dtos.types.vehicle.response;

import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleItemResponseData implements ItemResponseData {

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @Override
    public ItemType getItemType() {
        return ItemType.VEHICLE;
    }
}
