package com.claimscore.observability;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Hardening guard tests for observability components.
 *
 * <p>Covers PII masking edge cases and correlation ID propagation behaviour defined in
 * {@code sli_structured_logging_observability}.
 */
class ObservabilityHardeningTest {

  private final PiiMaskingConverter converter = new PiiMaskingConverter();
  private final CorrelationIdFilter filter = new CorrelationIdFilter();

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  /** Multiple SSN patterns in a single message are all replaced with {@code [MASKED]}. */
  @Test
  void masksMultipleSsnPatternsInOneMessage() {
    String message = "Member 123-45-6789 and dependent 987-65-4321 enrolled";
    String masked = converter.mask(message);
    assertThat(masked).doesNotContain("123-45-6789");
    assertThat(masked).doesNotContain("987-65-4321");
    assertThat(masked).contains("[MASKED]");
  }

  /** Digit sequences that do not match the SSN pattern pass through unchanged. */
  @Test
  void doesNotMaskNonSsnDigitPatterns() {
    String message = "Claim 12345 processed on 2024-01-15";
    String masked = converter.mask(message);
    assertThat(masked).isEqualTo(message);
  }

  /** Null input returns null; empty input returns an empty string. */
  @Test
  void handlesNullAndEmptyInput() {
    assertThat(converter.mask(null)).isNull();
    assertThat(converter.mask("")).isEmpty();
  }

  /** When no correlation ID header is supplied the filter generates a UUID-format value. */
  @Test
  void generatesUuidFormatCorrelationId() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    FilterChain chain = (req, res) -> {};

    filter.doFilter(request, response, chain);

    String correlationId = response.getHeader(CorrelationIdFilter.HEADER_NAME);
    assertThat(correlationId)
        .isNotNull()
        .matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
  }

  /** An existing correlation ID in the request header is echoed back unchanged. */
  @Test
  void propagatesExistingCorrelationIdUnchanged() throws ServletException, IOException {
    String existingId = "test-correlation-id-123";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(CorrelationIdFilter.HEADER_NAME, existingId);
    MockHttpServletResponse response = new MockHttpServletResponse();

    FilterChain chain = (req, res) -> {};

    filter.doFilter(request, response, chain);

    assertThat(response.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo(existingId);
  }
}
