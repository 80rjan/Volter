package com.volter.backend.item.application.dtos;

import com.volter.backend.item.application.dtos.details.*;
import com.volter.backend.item.domain.model.enums.ItemType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {

    private Long id;

    private ItemType itemType;

    private String description;

    private GoldItemDetailsResponse goldItemDetailsResponse;
    private ElectronicItemDetailsResponse electronicItemDetailsResponse;
    private VehicleItemDetailsResponse vehicleItemDetailsResponse;
    private WatchItemDetailsResponse watchItemDetailsResponse;
    private OtherItemDetailsResponse otherItemDetailsResponse;
}
