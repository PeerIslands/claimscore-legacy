package com.claimscore.reference;

/**
 * Enumeration of claim processing status codes.
 *
 * <p>Replaces the Oracle {@code LK_CLAIM_STATUS} lookup table. Each constant carries a
 * human-readable {@code displayName} suitable for UI rendering.
 */
public enum ClaimStatus {

    /** Claim has been received but not yet reviewed. */
    REC("Received"),

    /** Claim is pending additional information or review. */
    PND("Pending"),

    /** Claim has been fully approved. */
    APR("Approved"),

    /** Claim has been partially approved. */
    PAR("Partial Approval"),

    /** Claim has been denied. */
    DEN("Denied"),

    /** Claim has been paid. */
    PAID("Paid"),

    /** Claim has been reopened for re-adjudication. */
    REO("Reopened"),

    /** Claim has been voided. */
    VOID("Voided");

    private final String displayName;

    ClaimStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this status.
     *
     * @return non-null display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Looks up a {@link ClaimStatus} by its enum name (the Oracle status code).
     *
     * @param code the status code string, e.g. {@code "REC"} or {@code "APR"}
     * @return the matching constant
     * @throws IllegalArgumentException if no constant matches {@code code}
     */
    public static ClaimStatus fromCode(String code) {
        for (ClaimStatus s : values()) {
            if (s.name().equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown claim status: " + code);
    }
}
