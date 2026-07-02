package com.volter.shop.modules.notification.infrastructure.mapper;

import com.volter.shop.modules.notification.application.dto.NotificationResponse;
import com.volter.shop.modules.notification.domain.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
}
