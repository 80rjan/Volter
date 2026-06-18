package com.volter.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTtlMinutes,
        long preAuthTtlMinutes
) {
    public JwtProperties {
        if (accessTtlMinutes <= 0) accessTtlMinutes = 60 * 12;
        if (preAuthTtlMinutes <= 0) preAuthTtlMinutes = 5;
    }
}
