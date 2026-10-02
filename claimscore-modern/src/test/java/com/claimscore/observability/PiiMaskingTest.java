package com.claimscore.observability;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PiiMaskingConverter}.
 *
 * <p>Verifies that SSN patterns are masked and non-SSN strings pass through unchanged.
 */
class PiiMaskingTest {

  private final PiiMaskingConverter converter = new PiiMaskingConverter();

  /** SSN-like patterns in log messages are replaced with {@code [MASKED]}. */
  @Test
  void masksSSNPatternInLogMessages() {
    String input = "Processing claim for SSN 123-45-6789";
    String result = converter.mask(input);
    assertThat(result).doesNotContain("123-45-6789").contains("[MASKED]");
  }

  /** Strings that do not contain an SSN pattern pass through unchanged. */
  @Test
  void doesNotMaskNonSSNStrings() {
    String input = "Processing claim for claimant John Doe, claimId=CLAIM-001";
    String result = converter.mask(input);
    assertThat(result).isEqualTo(input);
  }
}
