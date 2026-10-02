package com.claimscore.reference;

/**
 * Enumeration of medical specialty codes.
 *
 * <p>Replaces the Oracle {@code LK_SPECIALTY} lookup table.
 */
public enum Specialty {

    /** Internal medicine specialty. */
    INTERNAL_MEDICINE("Internal Medicine"),

    /** Family practice specialty. */
    FAMILY_PRACTICE("Family Practice"),

    /** Cardiology specialty. */
    CARDIOLOGY("Cardiology"),

    /** Orthopedics specialty. */
    ORTHOPEDICS("Orthopedics"),

    /** Radiology specialty. */
    RADIOLOGY("Radiology");

    private final String displayName;

    Specialty(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this specialty.
     *
     * @return non-null display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Looks up a {@link Specialty} by its enum name.
     *
     * @param code the specialty code string, e.g. {@code "CARDIOLOGY"}
     * @return the matching constant
     * @throws IllegalArgumentException if no constant matches {@code code}
     */
    public static Specialty fromCode(String code) {
        for (Specialty s : values()) {
            if (s.name().equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown specialty: " + code);
    }
}
