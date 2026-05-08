package com.volter.shop.modules.inventory.web.response.baseitem;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.volter.shop.modules.inventory.web.response.types.electronic.ElectronicItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.gold.GoldItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.other.OtherItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.vehicle.VehicleItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.watch.WatchItemResponseData;
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
