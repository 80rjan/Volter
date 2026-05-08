package com.volter.shop.modules.alert.web.request;

import com.volter.shop.modules.alert.domain.enums.RiskAlertSeverity;
import com.volter.shop.modules.alert.domain.enums.RiskAlertType;

import java.util.Map;

public record RiskAlertCreationRequest(
        RiskAlertType type,
        RiskAlertSeverity severity,
        Map<String, Object> metadata,
        String summary
) { }