package com.volter.identity.modules.staff.infrastructure.mapper;

import com.volter.identity.modules.role.infrastructure.mapper.RoleMapper;
import com.volter.identity.modules.staff.application.dto.StaffRoleResponse;
import com.volter.identity.modules.staff.domain.model.StaffRole;
import com.volter.platform.modules.shop.application.dto.ShopResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface StaffRoleMapper {

    // only, so the caller resolves the shops once and supplies them as context, keyed by shop id.
    @Mapping(target = "shop", expression = "java(shops.get(staffRole.getShopId()))")
    StaffRoleResponse toResponse(StaffRole staffRole, @Context Map<Long, ShopResponse> shops);
}
