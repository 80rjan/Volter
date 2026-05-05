package com.volter.shop.security;

import com.volter.identity.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.ExpiredJwtException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JWT-based authentication, which is the core of the security configuration.
 * Uses ReflectionTestUtils to inject the @Value fields without loading the Spring context.
 */
class SecurityConfigBehaviorTest {

    private JwtUtil jwtUtil;

    // Must be at least 32 bytes for HS256
    private static final String SECRET = "test-secret-key-that-is-at-least-32-bytes-long!!";
    private static final long EXPIRATION_MS = 3_600_000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION_MS);
    }

    // =========================================================================
    // Token generation
    // =========================================================================

    @Nested
    @DisplayName("generateToken()")
    class GenerateToken {

        @Test
        @DisplayName("admin token (no shop) is non-null and non-blank")
        void adminToken_isNonNullAndNonBlank() {
            String token = jwtUtil.generateToken(1L);
            assertNotNull(token);
            assertFalse(token.isBlank());
        }

        @Test
        @DisplayName("staff token (with shop schema) is non-null and non-blank")
        void staffToken_withShopSchema_isNonNullAndNonBlank() {
            String token = jwtUtil.generateToken(1L, "shop_skopje");
            assertNotNull(token);
            assertFalse(token.isBlank());
        }

        @Test
        @DisplayName("different user IDs produce different tokens")
        void differentUserIds_produceDifferentTokens() {
            String token1 = jwtUtil.generateToken(1L);
            String token2 = jwtUtil.generateToken(2L);
            assertNotEquals(token1, token2);
        }

        @Test
        @DisplayName("different shop schemas produce different tokens")
        void differentShopSchemas_produceDifferentTokens() {
            String token1 = jwtUtil.generateToken(1L, "shop_skopje");
            String token2 = jwtUtil.generateToken(1L, "shop_bitola");
            assertNotEquals(token1, token2);
        }
    }

    // =========================================================================
    // Claim extraction
    // =========================================================================

    @Nested
    @DisplayName("Claim extraction")
    class ClaimExtraction {

        @Test
        @DisplayName("extractUserId returns the correct user ID")
        void extractUserId_returnsCorrectId() {
            String token = jwtUtil.generateToken(42L);
            assertEquals(42L, jwtUtil.extractUserId(token));
        }

        @Test
        @DisplayName("extractUserId works when shop schema is also embedded")
        void extractUserId_withShopSchema_returnsCorrectId() {
            String token = jwtUtil.generateToken(99L, "shop_bitola");
            assertEquals(99L, jwtUtil.extractUserId(token));
        }

        @Test
        @DisplayName("extractShopSchema returns the correct schema name")
        void extractShopSchema_returnsCorrectSchema() {
            String token = jwtUtil.generateToken(1L, "shop_skopje");
            assertEquals("shop_skopje", jwtUtil.extractShopSchema(token));
        }

        @Test
        @DisplayName("extractShopSchema returns null for admin token (no shop)")
        void extractShopSchema_adminToken_returnsNull() {
            String token = jwtUtil.generateToken(1L);
            assertNull(jwtUtil.extractShopSchema(token));
        }

        @Test
        @DisplayName("extractExpiration returns a future date for a fresh token")
        void extractExpiration_freshToken_returnsFutureDate() {
            String token = jwtUtil.generateToken(1L);
            assertTrue(jwtUtil.extractExpiration(token).after(new Date()));
        }
    }

    // =========================================================================
    // Expiry
    // =========================================================================

    @Nested
    @DisplayName("isTokenExpired()")
    class IsTokenExpired {

        @Test
        @DisplayName("fresh token is not expired")
        void freshToken_isNotExpired() {
            String token = jwtUtil.generateToken(1L);
            assertFalse(jwtUtil.isTokenExpired(token));
        }

        @Test
        @DisplayName("token with negative expiration causes isTokenExpired() to throw ExpiredJwtException")
        void negativeExpiration_throwsExpiredJwtException() {
            // JJWT's parseSignedClaims() throws ExpiredJwtException for expired tokens
            // rather than returning claims, so isTokenExpired() propagates that exception.
            ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
            String token = jwtUtil.generateToken(1L);
            assertThrows(ExpiredJwtException.class, () -> jwtUtil.isTokenExpired(token));
        }
    }

    // =========================================================================
    // validateToken(token, userId)
    // =========================================================================

    @Nested
    @DisplayName("validateToken(token, userId)")
    class ValidateTokenWithUserId {

        @Test
        @DisplayName("returns true for matching userId and non-expired token")
        void matchingUserId_validToken_returnsTrue() {
            String token = jwtUtil.generateToken(5L);
            assertTrue(jwtUtil.validateToken(token, 5L));
        }

        @Test
        @DisplayName("returns false when userId does not match")
        void mismatchedUserId_returnsFalse() {
            String token = jwtUtil.generateToken(5L);
            assertFalse(jwtUtil.validateToken(token, 99L));
        }

        @Test
        @DisplayName("expired token causes validateToken(token, userId) to throw ExpiredJwtException")
        void expiredToken_throwsExpiredJwtException() {
            // validateToken(String, Long) delegates to extractUserId + isTokenExpired,
            // neither of which catches ExpiredJwtException — use the single-arg overload for safe checks.
            ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
            String token = jwtUtil.generateToken(5L);
            assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateToken(token, 5L));
        }
    }

    // =========================================================================
    // validateToken(token) — overload used by the request filter
    // =========================================================================

    @Nested
    @DisplayName("validateToken(token) — filter overload")
    class ValidateTokenSingleArg {

        @Test
        @DisplayName("returns true for a valid, non-expired token")
        void validToken_returnsTrue() {
            String token = jwtUtil.generateToken(1L);
            assertTrue(jwtUtil.validateToken(token));
        }

        @Test
        @DisplayName("returns false for an expired token")
        void expiredToken_returnsFalse() {
            ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
            String token = jwtUtil.generateToken(1L);
            assertFalse(jwtUtil.validateToken(token));
        }

        @Test
        @DisplayName("returns false for a malformed token")
        void malformedToken_returnsFalse() {
            assertFalse(jwtUtil.validateToken("not.a.valid.jwt"));
        }

        @Test
        @DisplayName("returns false for an empty string")
        void emptyString_returnsFalse() {
            assertFalse(jwtUtil.validateToken(""));
        }

        @Test
        @DisplayName("returns false for a token signed with a different secret")
        void differentSecret_returnsFalse() {
            // Generate with one secret, then swap to another
            String token = jwtUtil.generateToken(1L);
            ReflectionTestUtils.setField(jwtUtil, "secret", "completely-different-secret-key-that-is-32-bytes!!");
            assertFalse(jwtUtil.validateToken(token));
        }
    }
}
