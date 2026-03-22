package com.volter.backend.item.application.dtos;

import com.volter.backend.item.application.dtos.details.ElectronicItemDetailsCreationRequest;
import com.volter.backend.item.application.dtos.details.GoldItemDetailsCreationRequest;
import com.volter.backend.item.domain.model.enums.ItemOriginType;
import com.volter.backend.item.domain.model.enums.ItemStatus;
import com.volter.backend.item.domain.model.enums.ItemType;
import com.volter.backend.item.application.dtos.details.OtherItemDetailsCreationRequest;
import com.volter.backend.item.application.dtos.details.VehicleItemDetailsCreationRequest;
import com.volter.backend.item.application.dtos.details.WatchItemDetailsCreationRequest;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCreationRequest {
    private ItemType itemType;

    private ItemOriginType itemOriginType;

    private ItemStatus itemStatus;

    private String description;

    private ElectronicItemDetailsCreationRequest electronicItemDetails;

    private GoldItemDetailsCreationRequest goldItemDetails;

    private OtherItemDetailsCreationRequest otherItemDetails;

    private VehicleItemDetailsCreationRequest vehicleItemDetails;

    private WatchItemDetailsCreationRequest watchItemDetails;
}
