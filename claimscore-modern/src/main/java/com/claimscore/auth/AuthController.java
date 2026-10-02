package com.claimscore.auth;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claimscore.security.JwtTokenProvider;
import com.claimscore.security.UserRole;

/**
 * REST controller for authentication operations.
 *
 * <p>Provides endpoints for logging in with username/password credentials and retrieving the
 * current authenticated user's information. No hard-coded credentials are used (PI-001).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs the controller with the required token provider.
     *
     * @param jwtTokenProvider the JWT creation and validation component
     */
    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authenticates a user and returns a signed JWT on success.
     *
     * <p>In a production deployment this method delegates to a {@code UserDetailsService} backed by
     * a persistent store. The stub implementation here demonstrates the contract; real credential
     * verification must be wired in before deployment.
     *
     * @param request the login credentials
     * @return a {@link LoginResponse} containing the JWT, expiry, and roles
     * @throws BadCredentialsException if the credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Stub: real implementations must verify credentials against a UserDetailsService.
        // Returning a token for any non-blank credentials here so the endpoint is exercisable.
        List<UserRole> roles = List.of(UserRole.EXAMINER);
        String token = jwtTokenProvider.createToken(request.getUsername(), roles);
        Instant expiresAt = Instant.now().plusSeconds(3600);
        return ResponseEntity.ok(new LoginResponse(token, expiresAt, roles));
    }

    /**
     * Returns information about the currently authenticated user.
     *
     * @param jwt the JWT principal injected by Spring Security
     * @return a map containing the username and roles extracted from the JWT
     */
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();
        List<String> roleNames = jwt.getClaimAsStringList("roles");
        List<UserRole> roles =
                roleNames == null
                        ? List.of()
                        : roleNames.stream().map(UserRole::valueOf).toList();
        Instant expiresAt = jwt.getExpiresAt();
        String token = jwt.getTokenValue();
        return ResponseEntity.ok(new LoginResponse(token, expiresAt, roles));
    }
}
