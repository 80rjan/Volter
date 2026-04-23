package com.volter.shop.modules.inventory.infrastructure;

import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.electronic.response.ElectronicItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.gold.response.GoldItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.other.response.OtherItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.vehicle.response.VehicleItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.watch.WatchItemResponseData;
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


