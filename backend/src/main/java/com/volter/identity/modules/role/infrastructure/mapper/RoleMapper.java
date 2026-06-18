package com.volter.identity.modules.role.infrastructure.mapper;

import com.volter.identity.modules.permission.infrastructure.mapper.PermissionMapper;
import com.volter.identity.modules.role.application.dto.RoleDetailedResponse;
import com.volter.identity.modules.role.application.dto.RoleResponse;
import com.volter.identity.modules.role.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public interface RoleMapper {

    @Mapping(target = "permissionCount", source = "permissions", qualifiedByName = "size")
    RoleResponse toResponse(Role role);

    RoleDetailedResponse toDetailedResponse(Role role);

    @Named("size")
    default int size(java.util.Set<?> set) {
        return set == null ? 0 : set.size();
    }
}
