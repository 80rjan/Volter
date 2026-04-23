package com.volter.shop.modules.customer.application.dto;

import com.volter.shop.modules.customer.domain.model.enums.CustomerRiskLevel;
import lombok.Data;

@Data
public class CustomerFilterDTO {
    private String name;
    private String phoneNumber;
    private String embg;
    private CustomerRiskLevel riskLevel;
}
