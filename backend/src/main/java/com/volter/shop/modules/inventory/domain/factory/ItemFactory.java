package com.volter.shop.modules.inventory.domain.factory;

import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.application.dtos.baseitem.request.ExistingItemReferenceRequest;
import com.volter.shop.modules.inventory.application.dtos.baseitem.request.ItemReferenceRequest;
import com.volter.shop.modules.inventory.application.dtos.baseitem.request.ItemRequestData;
import com.volter.shop.modules.inventory.application.dtos.baseitem.request.NewItemReferenceRequest;
import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemFactoryResult;
import com.volter.shop.modules.inventory.application.dtos.types.electronic.request.ElectronicItemRequestData;
import com.volter.shop.modules.inventory.application.dtos.types.gold.request.GoldItemRequestData;
import com.volter.shop.modules.inventory.application.dtos.types.other.request.OtherItemRequestData;
import com.volter.shop.modules.inventory.application.dtos.types.vehicle.request.VehicleItemRequestData;
import com.volter.shop.modules.inventory.application.dtos.types.watch.request.WatchItemRequestData;
import com.volter.shop.modules.inventory.domain.model.types.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ItemFactory {

    private final ItemService itemService;

    public ItemFactoryResult createOrGetItem(ItemReferenceRequest request) {
        return switch (request.getReferenceStrategy()) {
            case EXISTING -> getExistingItem((ExistingItemReferenceRequest) request);
            case NEW -> createItem(((NewItemReferenceRequest) request).getData());
        };
    }

    private ItemFactoryResult getExistingItem(ExistingItemReferenceRequest request) {
        return new ItemFactoryResult(itemService.getReferenceById(request.getItemId()), false);
    }

    public ItemFactoryResult createItem(ItemRequestData data) {
        return new ItemFactoryResult(
                switch (data.getItemType()) {
                    case GOLD -> createGoldItem((GoldItemRequestData) data);
                    case ELECTRONIC -> createElectronicItem((ElectronicItemRequestData) data);
                    case WATCH -> createWatchItem((WatchItemRequestData) data);
                    case VEHICLE -> createVehicleItem((VehicleItemRequestData) data);
                    case OTHER -> createOtherItem((OtherItemRequestData) data);
                }, true);
    }

    private GoldItem createGoldItem(GoldItemRequestData data) {
        return GoldItem.builder()
                .itemOriginType(data.getItemOriginType())
                .itemStatus(data.getItemStatus())
                .description(data.getDescription())
                .weightGrams(data.getWeightGrams())
                .pricePerGram(data.getPricePerGram())
                .carats(data.getCarats())
                .pieceType(data.getPieceType())
                .build();
    }

    private ElectronicItem createElectronicItem(ElectronicItemRequestData data) {
        return ElectronicItem.builder()
                .itemOriginType(data.getItemOriginType())
                .itemStatus(data.getItemStatus())
                .description(data.getDescription())
                .brand(data.getBrand())
                .category(data.getCategory())
                .year(data.getYear())
                .build();
    }

    private WatchItem createWatchItem(WatchItemRequestData data) {
        return WatchItem.builder()
                .itemOriginType(data.getItemOriginType())
                .itemStatus(data.getItemStatus())
                .description(data.getDescription())
                .brand(data.getBrand())
                .model(data.getModel())
                .material(data.getMaterial())
                .year(data.getYear())
                .originalBoxIncluded(data.getOriginalBoxIncluded())
                .originalPapersIncluded(data.getOriginalPapersIncluded())
                .warrantyCardIncluded(data.getWarrantyCardIncluded())
                .warrantyExpirationDate(data.getWarrantyExpirationDate())
                .functional(data.getFunctional())
                .serviceRequired(data.getServiceRequired())
                .build();
    }

    private VehicleItem createVehicleItem(VehicleItemRequestData data) {
        return VehicleItem.builder()
                .itemOriginType(data.getItemOriginType())
                .itemStatus(data.getItemStatus())
                .description(data.getDescription())
                .brand(data.getBrand())
                .model(data.getModel())
                .vehicleType(data.getVehicleType())
                .year(data.getYear())
                .registrationNumber(data.getRegistrationNumber())
                .mileage(data.getMileage())
                .serviceHistoryAvailable(data.getServiceHistoryAvailable())
                .lastServiceDate(data.getLastServiceDate())
                .registrationExpiryDate(data.getRegistrationExpiryDate())
                .numberOfKeys(data.getNumberOfKeys())
                .build();
    }

    private OtherItem createOtherItem(OtherItemRequestData data) {
        return OtherItem.builder()
                .itemOriginType(data.getItemOriginType())
                .itemStatus(data.getItemStatus())
                .description(data.getDescription())
                .category(data.getCategory())
                .build();
    }
}