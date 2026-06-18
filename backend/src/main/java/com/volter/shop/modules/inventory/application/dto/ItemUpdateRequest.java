package com.volter.shop.modules.inventory.application.dto;

import java.util.Map;

public record ItemUpdateRequest(
        String description,
        Map<String, Object> attributes
) {
}
