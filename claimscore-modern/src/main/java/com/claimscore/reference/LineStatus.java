package com.claimscore.reference;

/**
 * Enumeration of claim line status codes.
 *
 * <p>Replaces the Oracle {@code LK_LINE_STATUS} lookup table.
 */
public enum LineStatus {

    /** Line has been approved. */
    APR("Approved"),

    /** Line has been denied. */
    DEN("Denied"),

    /** Line is pending review. */
    PND("Pending");

    private final String displayName;

    LineStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this line status.
     *
     * @return non-null display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Looks up a {@link LineStatus} by its enum name.
     *
     * @param code the line status code string, e.g. {@code "APR"}
     * @return the matching constant
     * @throws IllegalArgumentException if no constant matches {@code code}
     */
    public static LineStatus fromCode(String code) {
        for (LineStatus s : values()) {
            if (s.name().equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown line status: " + code);
    }
}
