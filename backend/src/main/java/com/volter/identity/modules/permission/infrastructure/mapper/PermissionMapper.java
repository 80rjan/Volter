package com.volter.identity.modules.permission.infrastructure.mapper;

import com.volter.identity.modules.permission.application.dto.PermissionResponse;
import com.volter.identity.modules.permission.domain.model.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);
}
