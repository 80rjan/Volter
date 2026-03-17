package com.volter.backend.customer.dto;

import com.volter.backend.customer.enums.CustomerRiskLevel;
import lombok.Data;

@Data
public class CustomerFilterDTO {
    private String name;
    private String phoneNumber;
    private String embg;
    private CustomerRiskLevel riskLevel;
}
