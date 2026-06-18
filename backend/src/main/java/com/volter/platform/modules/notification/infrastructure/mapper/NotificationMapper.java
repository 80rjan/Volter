package com.volter.platform.modules.notification.infrastructure.mapper;

import com.volter.platform.modules.notification.application.dto.NotificationResponse;
import com.volter.platform.modules.notification.domain.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
}
