package com.volter.shop.modules.inventory.infrastructure.mapper;

import com.volter.shop.modules.inventory.application.dto.ItemResponse;
import com.volter.shop.modules.inventory.application.dto.ItemStatusHistoryResponse;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.ItemStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemResponse toResponse(Item item);

    @Mapping(target = "itemId", source = "history.item.id")
    @Mapping(target = "changedByStaffName", source = "changedByStaffName")
    ItemStatusHistoryResponse toResponse(ItemStatusHistory history, String changedByStaffName);
}
