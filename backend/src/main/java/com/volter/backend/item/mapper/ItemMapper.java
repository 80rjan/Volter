package com.volter.backend.item.mapper;

import com.volter.backend.electronicItem.mapper.ElectronicItemDetailsMapper;
import com.volter.backend.goldItem.mapper.GoldItemDetailsMapper;
import com.volter.backend.item.Item;
import com.volter.backend.item.dto.ItemDetailsResponse;
import com.volter.backend.item.dto.ItemResponse;
import com.volter.backend.otherItem.mapper.OtherItemDetailsMapper;
import com.volter.backend.vehicleItem.mapper.VehicleItemDetailsMapper;
import com.volter.backend.watchItem.mapper.WatchItemDetailsMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        GoldItemDetailsMapper.class,
        ElectronicItemDetailsMapper.class,
        VehicleItemDetailsMapper.class,
        WatchItemDetailsMapper.class,
        OtherItemDetailsMapper.class})
public abstract class ItemMapper {

    public abstract ItemResponse toResponse(Item item);

    public abstract ItemDetailsResponse toDetailsResponse(Item item);
}
