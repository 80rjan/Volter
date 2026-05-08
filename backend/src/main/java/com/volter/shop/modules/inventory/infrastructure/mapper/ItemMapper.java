package com.volter.shop.modules.inventory.infrastructure.mapper;

import com.volter.shop.modules.inventory.web.response.baseitem.ItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.electronic.ElectronicItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.gold.GoldItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.other.OtherItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.vehicle.VehicleItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.watch.WatchItemResponseData;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.types.*;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
        componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION
)
public abstract class ItemMapper {

    @SubclassMapping(source = GoldItem.class,       target = GoldItemResponseData.class)
    @SubclassMapping(source = ElectronicItem.class, target = ElectronicItemResponseData.class)
    @SubclassMapping(source = WatchItem.class,      target = WatchItemResponseData.class)
    @SubclassMapping(source = VehicleItem.class,    target = VehicleItemResponseData.class)
    @SubclassMapping(source = OtherItem.class,      target = OtherItemResponseData.class)
    @Named("toBaseResponse")
    public abstract ItemResponseData toResponseData(Item item);

    public abstract GoldItemResponseData       toGoldResponseData(GoldItem item);
    public abstract ElectronicItemResponseData toElectronicResponseData(ElectronicItem item);
    public abstract WatchItemResponseData      toWatchResponseData(WatchItem item);
    public abstract VehicleItemResponseData    toVehicleResponseData(VehicleItem item);
    public abstract OtherItemResponseData      toOtherResponseData(OtherItem item);

}


