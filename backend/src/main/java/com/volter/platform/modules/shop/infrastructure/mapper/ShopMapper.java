package com.volter.platform.modules.shop.infrastructure.mapper;

import com.volter.platform.modules.shop.application.dto.ShopResponse;
import com.volter.platform.modules.shop.domain.model.Shop;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopMapper {

    ShopResponse toResponse(Shop shop);
}
