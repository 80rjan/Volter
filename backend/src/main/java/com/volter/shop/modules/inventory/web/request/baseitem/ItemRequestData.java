package com.volter.shop.modules.inventory.web.request.baseitem;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.volter.shop.modules.inventory.web.request.types.electronic.ElectronicItemRequestData;
import com.volter.shop.modules.inventory.web.request.types.gold.GoldItemRequestData;
import com.volter.shop.modules.inventory.web.request.types.other.OtherItemRequestData;
import com.volter.shop.modules.inventory.web.request.types.vehicle.VehicleItemRequestData;
import com.volter.shop.modules.inventory.web.request.types.watch.WatchItemRequestData;
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