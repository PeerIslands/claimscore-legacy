package com.claimscore.observability;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

/**
 * Servlet filter that manages a per-request correlation ID.
 *
 * <p>Reads the {@code X-Correlation-ID} HTTP request header. If the header is absent, a random
 * UUID v4 is generated. The correlation ID is stored in the SLF4J {@link MDC} under the key
 * {@code correlationId} for the duration of the request, and echoed back in the
 * {@code X-Correlation-ID} response header. The MDC entry is always removed in a {@code finally}
 * block to prevent leakage across thread-pool reuse.
 */
public class CorrelationIdFilter implements Filter {

  /** MDC key used to store the correlation ID. */
  static final String MDC_KEY = "correlationId";

  /** HTTP header name for the correlation ID. */
  static final String HEADER_NAME = "X-Correlation-ID";

  /**
   * Processes the request by injecting a correlation ID into the MDC and response header.
   *
   * @param request the incoming servlet request
   * @param response the outgoing servlet response
   * @param chain the remaining filter chain
   * @throws IOException if an I/O error occurs during chain execution
   * @throws ServletException if a servlet error occurs during chain execution
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String correlationId = httpRequest.getHeader(HEADER_NAME);
    if (correlationId == null || correlationId.isBlank()) {
      correlationId = UUID.randomUUID().toString();
    }

    MDC.put(MDC_KEY, correlationId);
    httpResponse.setHeader(HEADER_NAME, correlationId);

    try {
      chain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }
}
