package com.volter.platform.modules.authevent.infrastructure.mapper;

import com.volter.platform.modules.authevent.application.dto.AuthEventResponse;
import com.volter.platform.modules.authevent.domain.model.AuthEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthEventMapper {

    AuthEventResponse toResponse(AuthEvent authEvent);
}
