package com.volter.backend.otherItem.mapper;

import com.volter.backend.otherItem.OtherItemDetails;
import com.volter.backend.otherItem.dto.OtherItemDetailsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class OtherItemDetailsMapper {

    public abstract OtherItemDetailsResponse toDTO(OtherItemDetails otherItemDetails);
}
