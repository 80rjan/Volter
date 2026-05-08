package com.volter.identity.web.request;

public record LoginRequest (
        String username,
        String password,
        Long shopId
) {}
