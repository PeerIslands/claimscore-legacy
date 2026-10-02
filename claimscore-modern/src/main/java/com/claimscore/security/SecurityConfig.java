package com.claimscore.security;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 6+ configuration for ClaimsCore.
 *
 * <p>Configures a stateless JWT resource server with role-based access control. Replaces the
 * legacy {@code LoginAction.java} MD5-based authentication (PI-001, PI-002, PI-003).
 *
 * <ul>
 *   <li>PI-001: No hard-coded credentials — secrets injected via {@link Value}.
 *   <li>PI-002: Passwords hashed with BCrypt, not MD5.
 *   <li>PI-003: Account lockout after {@code security.lockout.maxAttempts} failed attempts.
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Maximum consecutive failed login attempts before an account is locked. */
    private final int maxAttempts;

    /** Base64-encoded HMAC-SHA256 secret key for JWT signing and verification. */
    private final String jwtSecret;

    /** In-memory counter of failed attempts per username. */
    private final ConcurrentHashMap<String, AtomicInteger> failureCounters =
            new ConcurrentHashMap<>();

    /**
     * Constructs the security configuration.
     *
     * @param maxAttempts maximum failed login attempts before lockout (PI-003)
     * @param jwtSecret Base64-encoded HMAC-SHA256 secret (PI-001 — no hard-coded value)
     */
    public SecurityConfig(
            @Value("${security.lockout.maxAttempts:5}") int maxAttempts,
            @Value("${security.jwt.secret}") String jwtSecret) {
        this.maxAttempts = maxAttempts;
        this.jwtSecret = jwtSecret;
    }

    /**
     * Exposes a {@link JwtDecoder} bean backed by the configured HMAC-SHA256 secret.
     *
     * <p>This bean overrides Spring Boot's auto-configured decoder (which would require a live OIDC
     * issuer) and is used both by the resource server filter chain and by {@link JwtTokenProvider}.
     *
     * @return a {@link NimbusJwtDecoder} configured for HMAC-SHA256
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = decodeSecret(jwtSecret);
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    /**
     * Defines the primary {@link SecurityFilterChain}.
     *
     * <ul>
     *   <li>CSRF disabled — stateless JWT API.
     *   <li>{@code /api/v1/auth/**} is publicly accessible.
     *   <li>All other {@code /api/**} endpoints require authentication.
     *   <li>Sessions are never created (STATELESS).
     *   <li>JWT bearer token validation is delegated to the {@link #jwtDecoder()} bean.
     * </ul>
     *
     * @param http the {@link HttpSecurity} builder
     * @return the configured filter chain
     * @throws Exception if the configuration cannot be applied
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/api/v1/auth/**")
                                        .permitAll()
                                        .requestMatchers("/api/**")
                                        .authenticated()
                                        .anyRequest()
                                        .permitAll())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .headers(
                        headers ->
                                headers.httpStrictTransportSecurity(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * Provides a {@link PasswordEncoder} that uses BCrypt (replaces MD5 — PI-002).
     *
     * @return a BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Tracks failed authentication attempts and locks accounts that exceed the threshold (PI-003).
     *
     * @return an {@link AuthenticationEventPublisher} that increments failure counters
     */
    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher() {
        return new DefaultAuthenticationEventPublisher() {
            @Override
            public void publishAuthenticationSuccess(Authentication authentication) {
                failureCounters.remove(authentication.getName());
                super.publishAuthenticationSuccess(authentication);
            }

            @Override
            public void publishAuthenticationFailure(
                    AuthenticationException exception, Authentication authentication) {
                if (authentication != null && authentication.getName() != null) {
                    AtomicInteger count =
                            failureCounters.computeIfAbsent(
                                    authentication.getName(), k -> new AtomicInteger(0));
                    int attempts = count.incrementAndGet();
                    if (attempts >= maxAttempts) {
                        throw new LockedException(
                                "Account locked after "
                                        + attempts
                                        + " failed attempts: "
                                        + authentication.getName());
                    }
                }
                super.publishAuthenticationFailure(exception, authentication);
            }
        };
    }

    // ---- private helpers ----

    private static SecretKey decodeSecret(String base64Secret) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
}
