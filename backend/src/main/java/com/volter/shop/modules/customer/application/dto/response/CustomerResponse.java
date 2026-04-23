package com.volter.shop.modules.customer.application.dto.response;

import com.volter.shop.modules.customer.domain.model.enums.CustomerRiskLevel;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private String name;

    private String phoneNumber;

    private String reservePhoneNumber;

    private String embg;

    private String address;

    private String city;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CustomerRiskLevel riskLevel;

    private Integer totalPawnCount;

    private Integer totalSaleCount;

    private Integer lateRenewalCount;

    private Double avgDaysLate;

    private Integer onTimeRenewalCount;

    private Integer forfeitCount;

    private Integer redeemCount;
}
