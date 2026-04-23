package com.volter.shop.modules.inventory.infrastructure;

import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.electronic.response.ElectronicItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.gold.response.GoldItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.other.response.OtherItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.vehicle.response.VehicleItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.watch.WatchItemDetailedResponseData;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.types.*;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION
)
public abstract class ItemDetailedMapper {

    @SubclassMapping(source = GoldItem.class,       target = GoldItemDetailedResponseData.class)
    @SubclassMapping(source = ElectronicItem.class, target = ElectronicItemDetailedResponseData.class)
    @SubclassMapping(source = WatchItem.class,      target = WatchItemDetailedResponseData.class)
    @SubclassMapping(source = VehicleItem.class,    target = VehicleItemDetailedResponseData.class)
    @SubclassMapping(source = OtherItem.class,      target = OtherItemDetailedResponseData.class)
    @Named("toDetailedResponse")
    public abstract ItemDetailedResponseData toDetailedResponseData(Item item);

    public abstract GoldItemDetailedResponseData toGoldDetailedResponseData(GoldItem item);
    public abstract ElectronicItemDetailedResponseData toElectronicDetailedResponseData(ElectronicItem item);
    public abstract WatchItemDetailedResponseData toWatchDetailedResponseData(WatchItem item);
    public abstract VehicleItemDetailedResponseData toVehicleDetailedResponseData(VehicleItem item);
    public abstract OtherItemDetailedResponseData toOtherDetailedResponseData(OtherItem item);
}

