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
 * Unit tests for {@link CorrelationIdFilter}.
 *
 * <p>Verifies that the filter generates, propagates, and cleans up correlation IDs correctly.
 */
class CorrelationIdFilterTest {

  private final CorrelationIdFilter filter = new CorrelationIdFilter();

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  /** Generates a correlation ID when the request does not supply one. */
  @Test
  void generatesCorrelationIdWhenHeaderAbsent() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    FilterChain chain =
        (req, res) -> {
          // no-op — just let the filter do its work
        };

    filter.doFilter(request, response, chain);

    String correlationId = response.getHeader("X-Correlation-ID");
    assertThat(correlationId).isNotNull().isNotBlank();
  }

  /** Propagates the correlation ID supplied in the request header to the response. */
  @Test
  void propagatesCorrelationIdFromRequestHeader() throws ServletException, IOException {
    String expectedId = "test-correlation-id-123";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Correlation-ID", expectedId);
    MockHttpServletResponse response = new MockHttpServletResponse();

    FilterChain chain =
        (req, res) -> {
          // no-op
        };

    filter.doFilter(request, response, chain);

    assertThat(response.getHeader("X-Correlation-ID")).isEqualTo(expectedId);
  }

  /** MDC is cleared after the filter chain completes. */
  @Test
  void clearsMdcAfterRequest() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    FilterChain chain =
        (req, res) -> {
          // verify MDC is set during the chain
          assertThat(MDC.get("correlationId")).isNotNull();
        };

    filter.doFilter(request, response, chain);

    assertThat(MDC.get("correlationId")).isNull();
  }
}
