package com.volter.backend.item.dto;

import com.volter.backend.electronicItem.dto.ElectronicItemDetailsResponse;
import com.volter.backend.goldItem.dto.GoldItemDetailsResponse;
import com.volter.backend.item.enums.ItemType;
import com.volter.backend.otherItem.dto.OtherItemDetailsResponse;
import com.volter.backend.vehicleItem.dto.VehicleItemDetailsResponse;
import com.volter.backend.watchItem.dto.WatchItemDetailsResponse;
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
