package com.claimscore.reference;

/**
 * Enumeration of claim type codes.
 *
 * <p>Replaces the Oracle {@code LK_CLAIM_TYPE} lookup table.
 */
public enum ClaimType {

    /** Medical / physician claim. */
    MEDICAL("Medical"),

    /** Dental claim. */
    DENTAL("Dental"),

    /** Vision claim. */
    VISION("Vision"),

    /** Pharmacy / prescription claim. */
    PHARMACY("Pharmacy");

    private final String displayName;

    ClaimType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this claim type.
     *
     * @return non-null display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Looks up a {@link ClaimType} by its enum name.
     *
     * @param code the claim type code string, e.g. {@code "MEDICAL"}
     * @return the matching constant
     * @throws IllegalArgumentException if no constant matches {@code code}
     */
    public static ClaimType fromCode(String code) {
        for (ClaimType t : values()) {
            if (t.name().equals(code)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown claim type: " + code);
    }
}
