package com.volter.backend.goldItem.mapper;

import com.volter.backend.goldItem.GoldItemDetails;
import com.volter.backend.goldItem.dto.GoldItemDetailsCreationRequest;
import com.volter.backend.goldItem.dto.GoldItemDetailsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class GoldItemDetailsMapper {

    public abstract GoldItemDetailsResponse toDTO(GoldItemDetails goldItemDetails);

    public abstract GoldItemDetails toEntity(GoldItemDetailsCreationRequest request);
}
