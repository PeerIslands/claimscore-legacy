package com.claimscore.security;

/**
 * Enumeration of application roles used for role-based access control.
 *
 * <p>These roles are embedded as claims in the JWT and used by {@link SecurityConfig} to restrict
 * access to protected endpoints.
 */
public enum UserRole {
    /** Standard claims examiner — can view and process claims. */
    EXAMINER,

    /** Supervisor — can approve or escalate claims. */
    SUPERVISOR,

    /** Administrator — full system access. */
    ADMIN,

    /** Read-only observer — can view claims but cannot modify them. */
    READONLY
}
