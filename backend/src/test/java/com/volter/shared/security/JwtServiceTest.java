package com.volter.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the two-phase JWT issuing/parsing that underpins auth. No Spring
 * context: a {@link JwtService} is built directly over a test secret.
 */
class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-key-that-is-at-least-32-bytes-long!!";
    private final JwtService jwtService = new JwtService(new JwtProperties(SECRET, 60, 5));

    @Nested
    @DisplayName("issuing")
    class Issuing {

        @Test
        @DisplayName("an access token carries the staff subject, ACCESS scope and the shop id")
        void accessToken_carriesStaffScopeAndShop() {
            Claims claims = jwtService.parse(jwtService.issueAccessToken(42L, 7L));

            assertEquals(42L, jwtService.staffId(claims));
            assertEquals(TokenScope.ACCESS, jwtService.scope(claims));
            assertEquals(7L, jwtService.shopId(claims));
        }

        @Test
        @DisplayName("a pre-auth token has PRE_AUTH scope and no shop id")
        void preAuthToken_hasPreAuthScopeAndNoShop() {
            Claims claims = jwtService.parse(jwtService.issuePreAuthToken(42L));

            assertEquals(42L, jwtService.staffId(claims));
            assertEquals(TokenScope.PRE_AUTH, jwtService.scope(claims));
            assertNull(jwtService.shopId(claims));
        }
    }

    @Nested
    @DisplayName("parsing rejects bad tokens")
    class Parsing {

        @Test
        @DisplayName("a token signed with another key fails signature verification")
        void rejectsForeignSignature() {
            SecretKey otherKey = Keys.hmacShaKeyFor(
                    "a-totally-different-secret-key-of-32-bytes-plus!!".getBytes(StandardCharsets.UTF_8));
            String foreign = Jwts.builder()
                    .subject("42")
                    .claim("scope", "ACCESS")
                    .signWith(otherKey)
                    .compact();

            assertThrows(JwtException.class, () -> jwtService.parse(foreign));
        }

        @Test
        @DisplayName("an expired token is rejected even when correctly signed")
        void rejectsExpired() {
            SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
            long now = System.currentTimeMillis();
            String expired = Jwts.builder()
                    .subject("42")
                    .claim("scope", "ACCESS")
                    .issuedAt(new Date(now - 120_000))
                    .expiration(new Date(now - 60_000))
                    .signWith(key)
                    .compact();

            assertThrows(JwtException.class, () -> jwtService.parse(expired));
        }

        @Test
        @DisplayName("a malformed token is rejected")
        void rejectsMalformed() {
            assertThrows(JwtException.class, () -> jwtService.parse("not.a.valid.jwt"));
        }
    }
}
