package com.claimscore.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Unit tests for security configuration components. Tests the BCrypt password encoder and JWT
 * token provider in isolation, without loading the full Spring context.
 */
class SecurityConfigTest {

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  /** BCrypt encoder must produce hashes starting with the BCrypt prefix. */
  @Test
  void bcryptEncoderProducesBcryptHash() {
    String hash = encoder.encode("testPassword");
    assertThat(hash).startsWith("$2a$");
  }

  /** BCrypt encoder must verify correct passwords. */
  @Test
  void bcryptEncoderMatchesCorrectPassword() {
    String hash = encoder.encode("testPassword");
    assertThat(encoder.matches("testPassword", hash)).isTrue();
  }

  /** BCrypt encoder must reject incorrect passwords (not MD5-based). */
  @Test
  void bcryptEncoderRejectsWrongPassword() {
    String hash = encoder.encode("testPassword");
    assertThat(encoder.matches("wrongPassword", hash)).isFalse();
  }
}
