package com.volter.backend.customer.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCreationRequest {
    private String name;

    private String phoneNumber;

    private String reservePhoneNumber;

    private String embg;

    private String address;

    private String city;
}
