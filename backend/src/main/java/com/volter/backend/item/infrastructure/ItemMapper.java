package com.volter.backend.item.infrastructure;

import com.volter.backend.item.application.dtos.details.*;
import com.volter.backend.item.domain.model.details.*;
import com.volter.backend.itemdetails.mappers.ElectronicItemDetailsMapper;
import com.volter.backend.itemdetails.mappers.GoldItemDetailsMapper;
import com.volter.backend.item.domain.model.Item;
import com.volter.backend.item.application.dtos.ItemCreationRequest;
import com.volter.backend.item.application.dtos.ItemDetailsResponse;
import com.volter.backend.item.application.dtos.ItemResponse;
import com.volter.backend.itemdetails.mappers.OtherItemDetailsMapper;
import com.volter.backend.itemdetails.mappers.VehicleItemDetailsMapper;
import com.volter.backend.itemdetails.mappers.WatchItemDetailsMapper;
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

    public abstract Item toEntity(ItemCreationRequest request);

    // DETAILS

    // Electronic Item
    public abstract ElectronicItemDetailsResponse toDTO(ElectronicItemDetails electronicItemDetails);
    public abstract ElectronicItemDetails toEntity(ElectronicItemDetailsCreationRequest request);

    // Gold Item
    public abstract GoldItemDetailsResponse toDTO(GoldItemDetails goldItemDetails);
    public abstract GoldItemDetails toEntity(GoldItemDetailsCreationRequest request);

    // Other Item
    public abstract OtherItemDetailsResponse toDTO(OtherItemDetails otherItemDetails);
    public abstract OtherItemDetails toEntity(OtherItemDetailsCreationRequest request);

    // Vehicle Item
    public abstract VehicleItemDetailsResponse toDTO(VehicleItemDetails vehicleItemDetails);
    public abstract VehicleItemDetails toEntity(VehicleItemDetailsCreationRequest request);

    // Watch Item
    public abstract WatchItemDetailsResponse toDTO(WatchItemDetails watchItemDetails);
    public abstract WatchItemDetails toEntity(WatchItemDetailsCreationRequest request);
}
