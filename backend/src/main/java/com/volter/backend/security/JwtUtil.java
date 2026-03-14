package com.volter.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // in milliseconds

    private SecretKey getSigningKey() {  // Change Key to SecretKey
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate JWT token with staffId and role
     */
    public String generateToken(Long staffId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("staffId", staffId);
        claims.put("role", role);

        return createToken(claims, staffId.toString());
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
     * Extract staffId from token
     */
    public Long extractStaffId(String token) {
        Claims claims = extractAllClaims(token);
        Object staffIdObj = claims.get("staffId");

        if (staffIdObj instanceof Integer) {
            return ((Integer) staffIdObj).longValue();
        } else if (staffIdObj instanceof Long) {
            return (Long) staffIdObj;
        }
        throw new IllegalArgumentException("Invalid staffId in token");
    }

    /**
     * Extract role from token
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
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
    public Boolean validateToken(String token, Long staffId) {
        final Long tokenStaffId = extractStaffId(token);
        return (tokenStaffId.equals(staffId) && !isTokenExpired(token));
    }

    /**
     * Validate token without staffId check (for filter)
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}