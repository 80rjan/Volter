package com.volter.shop.modules.inventory.web.response.baseitem;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.volter.shop.modules.inventory.web.response.types.electronic.ElectronicItemDetailedResponseData;
import com.volter.shop.modules.inventory.web.response.types.gold.GoldItemDetailedResponseData;
import com.volter.shop.modules.inventory.web.response.types.other.OtherItemDetailedResponseData;
import com.volter.shop.modules.inventory.web.response.types.vehicle.VehicleItemDetailedResponseData;
import com.volter.shop.modules.inventory.web.response.types.watch.WatchItemDetailedResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "itemType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GoldItemDetailedResponseData.class, name = ItemType.GOLD_VALUE),
        @JsonSubTypes.Type(value = ElectronicItemDetailedResponseData.class, name = ItemType.ELECTRONIC_VALUE),
        @JsonSubTypes.Type(value = WatchItemDetailedResponseData.class, name = ItemType.WATCH_VALUE),
        @JsonSubTypes.Type(value = VehicleItemDetailedResponseData.class, name = ItemType.VEHICLE_VALUE),
        @JsonSubTypes.Type(value = OtherItemDetailedResponseData.class, name = ItemType.OTHER_VALUE)
})
public interface ItemDetailedResponseData extends ItemResponseData {
}
