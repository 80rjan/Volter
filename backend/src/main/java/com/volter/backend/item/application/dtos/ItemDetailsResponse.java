package com.volter.backend.item.application.dtos;

import com.volter.backend.item.application.dtos.details.*;
import com.volter.backend.item.domain.model.enums.ItemOriginType;
import com.volter.backend.item.domain.model.enums.ItemStatus;
import com.volter.backend.item.domain.model.enums.ItemType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDetailsResponse {

    private ItemType itemType;

    private ItemOriginType itemOriginType;

    private LocalDateTime createdAt;

    private ItemStatus itemStatus;

    private String description;

    private GoldItemDetailsResponse goldItemDetails;

    private ElectronicItemDetailsResponse electronicItemDetails;

    private VehicleItemDetailsResponse vehicleItemDetails;

    private WatchItemDetailsResponse watchItemDetails;

    private OtherItemDetailsResponse otherItemDetails;
}
