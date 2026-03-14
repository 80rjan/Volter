package com.volter.backend.customer.dto;

import com.volter.backend.customer.enums.CustomerRiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
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
