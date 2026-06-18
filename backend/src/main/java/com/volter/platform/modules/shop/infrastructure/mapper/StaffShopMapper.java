package com.volter.platform.modules.shop.infrastructure.mapper;

import com.volter.platform.modules.shop.application.dto.StaffShopResponse;
import com.volter.platform.modules.shop.domain.model.StaffShop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StaffShopMapper {

    @Mapping(target = "shopId", source = "shop.id")
    @Mapping(target = "shopName", source = "shop.name")
    StaffShopResponse toResponse(StaffShop staffShop);
}
