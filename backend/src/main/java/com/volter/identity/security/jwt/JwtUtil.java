package com.volter.identity.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // I only have access token with 12hrs expiration
    // With this the user is logged on for 12hrs (which is more than his shift, this is for not being logged out during shift)
    // And if the user logs out or comes the next day, he needs to log in again, which is good for security (no refresh token needed)

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // in milliseconds

    private SecretKey getSigningKey() {  // Change Key to SecretKey
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate token for ADMIN (no shop)
     */
    public String generateToken(Long userId) {
        return generateToken(userId, null);
    }

    /**
     * Generate token for MANAGER/STAFF (with shop schema)
     */
    public String generateToken(Long userId, String shopSchema) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        if (shopSchema != null) {   // can be null becaues public is default for ADMIN
            claims.put("shopSchema", shopSchema);  // e.g. "shop_skopje"
        }
        return createToken(claims, userId.toString());
    }

    /**
     * Extract shopSchema from token (null for ADMIN)
     */
    public String extractShopSchema(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("shopSchema", String.class); // returns null if not present
    }

    /**
     * Extract userId from token
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object userIdObj = claims.get("userId");

        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        throw new IllegalArgumentException("Invalid userId in token");
    }


    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token
     */
    public Boolean validateToken(String token, Long userId) {
        final Long tokenUserId = extractUserId(token);
        return (tokenUserId.equals(userId) && !isTokenExpired(token));
    }

    /**
     * Validate token without userId check (for filter)
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}