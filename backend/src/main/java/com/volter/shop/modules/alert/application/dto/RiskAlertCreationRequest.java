package com.volter.shop.modules.alert.application.dto;

import com.volter.shop.modules.alert.domain.model.enums.RiskAlertSeverity;
import com.volter.shop.modules.alert.domain.model.enums.RiskAlertType;

import java.util.Map;

public record RiskAlertCreationRequest(
        RiskAlertType type,
        RiskAlertSeverity severity,
        Map<String, Object> metadata,
        String summary
) { }