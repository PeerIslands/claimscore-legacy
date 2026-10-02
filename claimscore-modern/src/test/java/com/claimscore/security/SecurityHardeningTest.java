package com.claimscore.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Guard tests for security edge cases: BCrypt boundary inputs, JWT tampering, null/blank JWT
 * inputs, and the completeness of the {@link UserRole} enum.
 *
 * <p>All tests run without a Spring context — components are instantiated directly.
 */
class SecurityHardeningTest {

  /** A 256-bit (32-byte) Base64-encoded secret suitable for HMAC-SHA256. */
  private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2g=";

  private BCryptPasswordEncoder encoder;
  private JwtTokenProvider provider;

  /** Initialise fresh instances before each test. */
  @BeforeEach
  void setUp() {
    encoder = new BCryptPasswordEncoder();
    provider = new JwtTokenProvider(SECRET, 3600L);
  }

  /**
   * BCrypt must handle an empty password without throwing during {@code encode}, producing a valid
   * BCrypt hash. Spring Security's {@code BCryptPasswordEncoder.matches} returns {@code false} for
   * an empty raw password (it treats blank input as a non-match), so a non-empty password must also
   * not match the empty-password hash.
   */
  @Test
  void bcryptRejectsEmptyPassword() {
    String hash = encoder.encode("");
    assertThat(hash).isNotNull().startsWith("$2a$");
    // Spring Security BCryptPasswordEncoder.matches returns false for empty raw password
    assertThat(encoder.matches("", hash)).isFalse();
    assertThat(encoder.matches("notempty", hash)).isFalse();
  }

  /**
   * BCrypt uses a random salt, so encoding the same plaintext twice must produce two distinct
   * hashes.
   */
  @Test
  void bcryptProducesDifferentHashesForSameInput() {
    String hash1 = encoder.encode("password");
    String hash2 = encoder.encode("password");
    assertThat(hash1).isNotEqualTo(hash2);
  }

  /** A token whose signature has been tampered with must be rejected by {@link JwtTokenProvider}. */
  @Test
  void jwtWithTamperedSignatureIsInvalid() {
    String token = provider.createToken("alice", List.of(UserRole.EXAMINER));
    String tampered = token.substring(0, token.length() - 5) + "XXXXX";
    assertThat(provider.isValid(tampered)).isFalse();
  }

  /** Null, blank, and structurally invalid strings must all be rejected as invalid tokens. */
  @Test
  void jwtWithNullInputIsInvalid() {
    assertThat(provider.isValid(null)).isFalse();
    assertThat(provider.isValid("")).isFalse();
    assertThat(provider.isValid("not.a.jwt")).isFalse();
  }

  /** The {@link UserRole} enum must contain exactly the four required roles. */
  @Test
  void userRoleEnumHasAllRequiredRoles() {
    assertThat(UserRole.values())
        .containsExactlyInAnyOrder(
            UserRole.EXAMINER, UserRole.SUPERVISOR, UserRole.ADMIN, UserRole.READONLY);
  }
}
