package com.volter.backend.customer.application.dto;

import com.volter.backend.customer.domain.model.enums.CustomerRiskLevel;
import lombok.Data;

@Data
public class CustomerFilterDTO {
    private String name;
    private String phoneNumber;
    private String embg;
    private CustomerRiskLevel riskLevel;
}
