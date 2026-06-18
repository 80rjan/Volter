package com.volter.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final String CLAIM_SCOPE = "scope";
    private static final String CLAIM_SHOP_ID = "shopId";

    private final SecretKey key;
    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String issueAccessToken(Long staffId, Long shopId) {
        return issue(staffId, TokenScope.ACCESS, shopId, Duration.ofMinutes(properties.accessTtlMinutes()));
    }

    public String issuePreAuthToken(Long staffId) {
        return issue(staffId, TokenScope.PRE_AUTH, null, Duration.ofMinutes(properties.preAuthTtlMinutes()));
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long staffId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public TokenScope scope(Claims claims) {
        return TokenScope.valueOf(claims.get(CLAIM_SCOPE, String.class));
    }

    public Long shopId(Claims claims) {
        Object raw = claims.get(CLAIM_SHOP_ID);
        return raw == null ? null : Long.valueOf(raw.toString());
    }

    private String issue(Long staffId, TokenScope scope, Long shopId, Duration ttl) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .subject(String.valueOf(staffId))
                .claim(CLAIM_SCOPE, scope.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)));
        if (shopId != null) {
            builder.claim(CLAIM_SHOP_ID, shopId);
        }
        return builder.signWith(key).compact();
    }
}
