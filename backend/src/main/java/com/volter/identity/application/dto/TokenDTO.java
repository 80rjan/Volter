package com.volter.identity.application.dto;

public record TokenDTO(
        String token,
        String role,
        String shopSchema,
        String shopName
) {
}
