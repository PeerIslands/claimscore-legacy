package com.claimscore.observability;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Spring configuration for observability infrastructure.
 *
 * <p>Registers the {@link CorrelationIdFilter} as a servlet filter applied to all URL patterns,
 * and pre-creates Micrometer {@link Counter} meters for key claims lifecycle events so that they
 * appear in the metrics endpoint even before the first event is recorded.
 */
@Configuration
public class ObservabilityConfig {

  /**
   * Registers {@link CorrelationIdFilter} to intercept every incoming HTTP request.
   *
   * @return a configured {@link FilterRegistrationBean} wrapping the correlation ID filter
   */
  @Bean
  public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
    FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new CorrelationIdFilter());
    registration.addUrlPatterns("/*");
    registration.setName("correlationIdFilter");
    registration.setOrder(1);
    return registration;
  }

  /**
   * Pre-creates a Micrometer counter for submitted claims.
   *
   * @param registry the Micrometer {@link MeterRegistry} managed by Spring Boot Actuator
   * @return the {@code claims.submitted} counter
   */
  @Bean
  public Counter claimsSubmittedCounter(MeterRegistry registry) {
    return Counter.builder("claims.submitted")
        .description("Total number of claims submitted")
        .register(registry);
  }

  /**
   * Pre-creates a Micrometer counter for adjudicated claims.
   *
   * @param registry the Micrometer {@link MeterRegistry} managed by Spring Boot Actuator
   * @return the {@code claims.adjudicated} counter
   */
  @Bean
  public Counter claimsAdjudicatedCounter(MeterRegistry registry) {
    return Counter.builder("claims.adjudicated")
        .description("Total number of claims adjudicated")
        .register(registry);
  }

  /**
   * Pre-creates a Micrometer counter for rejected claims.
   *
   * @param registry the Micrometer {@link MeterRegistry} managed by Spring Boot Actuator
   * @return the {@code claims.rejected} counter
   */
  @Bean
  public Counter claimsRejectedCounter(MeterRegistry registry) {
    return Counter.builder("claims.rejected")
        .description("Total number of claims rejected")
        .register(registry);
  }
}
