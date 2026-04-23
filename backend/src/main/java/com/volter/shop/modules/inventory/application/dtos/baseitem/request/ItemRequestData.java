package com.volter.shop.modules.inventory.application.dtos.baseitem.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.volter.shop.modules.inventory.application.dtos.types.electronic.request.ElectronicItemRequestData;
import com.volter.shop.modules.inventory.application.dtos.types.gold.request.GoldItemRequestData;
import com.volter.shop.modules.inventory.application.dtos.types.other.request.OtherItemRequestData;
import com.volter.shop.modules.inventory.application.dtos.types.vehicle.request.VehicleItemRequestData;
import com.volter.shop.modules.inventory.application.dtos.types.watch.request.WatchItemRequestData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "itemType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GoldItemRequestData.class, name = ItemType.GOLD_VALUE),
        @JsonSubTypes.Type(value = ElectronicItemRequestData.class, name = ItemType.ELECTRONIC_VALUE),
        @JsonSubTypes.Type(value = WatchItemRequestData.class, name = ItemType.WATCH_VALUE),
        @JsonSubTypes.Type(value = VehicleItemRequestData.class, name = ItemType.VEHICLE_VALUE),
        @JsonSubTypes.Type(value = OtherItemRequestData.class, name = ItemType.OTHER_VALUE)
})
public interface ItemRequestData {
    ItemType getItemType();
}