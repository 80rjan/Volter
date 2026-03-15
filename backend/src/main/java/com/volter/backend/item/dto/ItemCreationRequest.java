package com.volter.backend.item.dto;

import com.volter.backend.electronicItem.dto.ElectronicItemDetailsCreationRequest;
import com.volter.backend.goldItem.dto.GoldItemDetailsCreationRequest;
import com.volter.backend.item.enums.ItemOriginType;
import com.volter.backend.item.enums.ItemStatus;
import com.volter.backend.item.enums.ItemType;
import com.volter.backend.otherItem.dto.OtherItemDetailsCreationRequest;
import com.volter.backend.vehicleItem.dto.VehicleItemDetailsCreationRequest;
import com.volter.backend.watchItem.dto.WatchItemDetailsCreationRequest;
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
