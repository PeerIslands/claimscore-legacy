package com.claimscore.auth;

import java.time.Instant;
import java.util.List;

import com.claimscore.security.UserRole;

/**
 * Response body returned by the {@code POST /api/v1/auth/login} endpoint on successful
 * authentication.
 */
public class LoginResponse {

    /** The signed JWT bearer token. */
    private String token;

    /** The UTC instant at which the token expires. */
    private Instant expiresAt;

    /** The roles granted to the authenticated user. */
    private List<UserRole> roles;

    /** Default no-arg constructor required for JSON serialisation. */
    public LoginResponse() {}

    /**
     * Constructs a login response.
     *
     * @param token the signed JWT string
     * @param expiresAt the token expiry instant
     * @param roles the user's granted roles
     */
    public LoginResponse(String token, Instant expiresAt, List<UserRole> roles) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.roles = roles;
    }

    /**
     * Returns the JWT token.
     *
     * @return the token string
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the JWT token.
     *
     * @param token the token string
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Returns the token expiry instant.
     *
     * @return the expiry instant
     */
    public Instant getExpiresAt() {
        return expiresAt;
    }

    /**
     * Sets the token expiry instant.
     *
     * @param expiresAt the expiry instant
     */
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Returns the list of roles granted to the user.
     *
     * @return the roles list
     */
    public List<UserRole> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles.
     *
     * @param roles the roles list
     */
    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }
}
