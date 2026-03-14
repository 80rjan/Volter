package com.volter.backend.watchItem.mapper;

import com.volter.backend.watchItem.WatchItemDetails;
import com.volter.backend.watchItem.dto.WatchItemDetailsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class WatchItemDetailsMapper {

    public abstract WatchItemDetailsResponse toDTO(WatchItemDetails watchItemDetails);
}
