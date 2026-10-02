package com.claimscore.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for the {@code POST /api/v1/auth/login} endpoint.
 *
 * <p>Credentials are validated with Jakarta Bean Validation before the authentication attempt is
 * made. No hard-coded defaults are present (PI-001).
 */
public class LoginRequest {

    /** The user's login name. Must not be blank. */
    @NotBlank(message = "Username must not be blank")
    @Size(max = 255, message = "Username must not exceed 255 characters")
    private String username;

    /** The user's password. Must not be blank. */
    @NotBlank(message = "Password must not be blank")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String password;

    /** Default no-arg constructor required for JSON deserialisation. */
    public LoginRequest() {}

    /**
     * Constructs a login request with the given credentials.
     *
     * @param username the user's login name
     * @param password the user's password
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
