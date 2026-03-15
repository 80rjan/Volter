package com.volter.backend.electronicItem.mapper;

import com.volter.backend.electronicItem.ElectronicItemDetails;
import com.volter.backend.electronicItem.dto.ElectronicItemDetailsCreationRequest;
import com.volter.backend.electronicItem.dto.ElectronicItemDetailsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ElectronicItemDetailsMapper {

    public abstract ElectronicItemDetailsResponse toDTO(ElectronicItemDetails electronicItemDetails);

    public abstract ElectronicItemDetails toEntity(ElectronicItemDetailsCreationRequest request);
}
