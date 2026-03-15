package com.volter.backend.vehicleItem.mapper;

import com.volter.backend.vehicleItem.VehicleItemDetails;
import com.volter.backend.vehicleItem.dto.VehicleItemDetailsCreationRequest;
import com.volter.backend.vehicleItem.dto.VehicleItemDetailsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class VehicleItemDetailsMapper {

    public abstract VehicleItemDetailsResponse toDTO(VehicleItemDetails vehicleItemDetails);

    public abstract VehicleItemDetails toEntity(VehicleItemDetailsCreationRequest request);
}
