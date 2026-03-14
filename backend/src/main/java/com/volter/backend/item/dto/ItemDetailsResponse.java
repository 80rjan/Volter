package com.volter.backend.item.dto;

import com.volter.backend.electronicItem.dto.ElectronicItemDetailsResponse;
import com.volter.backend.goldItem.dto.GoldItemDetailsResponse;
import com.volter.backend.item.enums.ItemOriginType;
import com.volter.backend.item.enums.ItemStatus;
import com.volter.backend.item.enums.ItemType;
import com.volter.backend.otherItem.dto.OtherItemDetailsResponse;
import com.volter.backend.vehicleItem.dto.VehicleItemDetailsResponse;
import com.volter.backend.watchItem.dto.WatchItemDetailsResponse;
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
