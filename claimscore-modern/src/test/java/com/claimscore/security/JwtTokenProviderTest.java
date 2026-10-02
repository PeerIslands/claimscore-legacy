package com.claimscore.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JwtTokenProvider} covering token creation, validation, expiry, and claim
 * extraction.
 */
class JwtTokenProviderTest {

    /** A 256-bit (32-byte) Base64-encoded secret suitable for HMAC-SHA256. */
    private static final String SECRET =
            "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2g=";

    private static final long EXPIRY_SECONDS = 3600L;

    private JwtTokenProvider provider;

    /** Initialise a fresh provider before each test. */
    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET, EXPIRY_SECONDS);
    }

    /** Token must be non-null and non-blank. */
    @Test
    void createsValidJwtToken() {
        String token = provider.createToken("alice", List.of(UserRole.EXAMINER));

        assertThat(token).isNotNull().isNotBlank();
    }

    /** A freshly created token must pass validation. */
    @Test
    void validatesValidToken() {
        String token = provider.createToken("alice", List.of(UserRole.EXAMINER));

        assertThat(provider.isValid(token)).isTrue();
    }

    /** A token created with 1-second expiry must fail validation after expiry. */
    @Test
    void rejectsExpiredToken() throws InterruptedException {
        JwtTokenProvider shortLived = new JwtTokenProvider(SECRET, 1L);
        String token = shortLived.createToken("alice", List.of(UserRole.EXAMINER));
        // Wait for the token to expire
        Thread.sleep(1500);

        assertThat(shortLived.isValid(token)).isFalse();
    }

    /** The username embedded in the token must round-trip correctly. */
    @Test
    void extractsUsernameFromToken() {
        String token = provider.createToken("bob", List.of(UserRole.SUPERVISOR));

        assertThat(provider.extractUsername(token)).isEqualTo("bob");
    }
}
