package com.volter.identity.application.dto;

public record LoginRequest (
        String username,
        String password,
        Long shopId
) {}
