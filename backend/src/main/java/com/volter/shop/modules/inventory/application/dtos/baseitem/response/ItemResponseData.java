package com.volter.shop.modules.inventory.application.dtos.baseitem.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.volter.shop.modules.inventory.application.dtos.types.electronic.response.ElectronicItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.gold.response.GoldItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.other.response.OtherItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.vehicle.response.VehicleItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.watch.WatchItemResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "itemType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GoldItemResponseData.class, name = ItemType.GOLD_VALUE),
        @JsonSubTypes.Type(value = ElectronicItemResponseData.class, name = ItemType.ELECTRONIC_VALUE),
        @JsonSubTypes.Type(value = WatchItemResponseData.class, name = ItemType.WATCH_VALUE),
        @JsonSubTypes.Type(value = VehicleItemResponseData.class, name = ItemType.VEHICLE_VALUE),
        @JsonSubTypes.Type(value = OtherItemResponseData.class, name = ItemType.OTHER_VALUE)
})
public interface ItemResponseData {
    ItemType getItemType();
}
