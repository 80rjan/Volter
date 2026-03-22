package com.volter.backend.customer.application.dto;

import com.volter.backend.customer.domain.model.enums.CustomerRiskLevel;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {

    private String name;

    private String phoneNumber;

    private String reservePhoneNumber;

    private String embg;

    private String address;

    private String city;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CustomerRiskLevel riskLevel;
}
