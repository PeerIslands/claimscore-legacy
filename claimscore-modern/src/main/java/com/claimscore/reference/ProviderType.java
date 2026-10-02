package com.claimscore.reference;

/**
 * Enumeration of provider type codes.
 *
 * <p>Replaces the Oracle {@code LK_PROVIDER_TYPE} lookup table.
 */
public enum ProviderType {

    /** Individual physician. */
    PHYSICIAN("Physician"),

    /** Hospital facility. */
    HOSPITAL("Hospital"),

    /** Specialist physician. */
    SPECIALIST("Specialist"),

    /** Clinical laboratory. */
    LAB("Lab"),

    /** Pharmacy. */
    PHARMACY("Pharmacy");

    private final String displayName;

    ProviderType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this provider type.
     *
     * @return non-null display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Looks up a {@link ProviderType} by its enum name.
     *
     * @param code the provider type code string, e.g. {@code "PHYSICIAN"}
     * @return the matching constant
     * @throws IllegalArgumentException if no constant matches {@code code}
     */
    public static ProviderType fromCode(String code) {
        for (ProviderType t : values()) {
            if (t.name().equals(code)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown provider type: " + code);
    }
}
