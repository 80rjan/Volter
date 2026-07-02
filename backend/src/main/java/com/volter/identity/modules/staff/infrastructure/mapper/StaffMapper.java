package com.volter.identity.modules.staff.infrastructure.mapper;

import com.volter.identity.modules.staff.application.dto.StaffDetailedResponse;
import com.volter.identity.modules.staff.application.dto.StaffResponse;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.platform.modules.shop.application.dto.ShopResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring", uses = StaffRoleMapper.class)
public interface StaffMapper {

    @Mapping(target = "managerId", source = "manager.id")
    StaffResponse toResponse(Staff staff);

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "roleGrants", source = "staffRoles")
    StaffDetailedResponse toDetailedResponse(Staff staff, @Context Map<Long, ShopResponse> shops);
}
