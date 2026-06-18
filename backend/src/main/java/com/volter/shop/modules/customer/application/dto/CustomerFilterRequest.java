package com.volter.shop.modules.customer.application.dto;

public record CustomerFilterRequest(
        String fullName,
        String nationalId,
        String phone,
        String city
) {
}
