package com.claimscore.reference;

/**
 * Enumeration of denial reason codes with member-facing explanatory text.
 *
 * <p>Replaces the Oracle {@code LK_DENIAL_REASON} lookup table. Each constant carries the
 * {@code memberFacingText} that is displayed to members on Explanation of Benefits (EOB)
 * documents.
 */
public enum DenialReasonCode {

    /** Claim was not submitted within the required filing period. */
    TIMELY("Timely filing exceeded"),

    /** Claim is a duplicate of a previously processed claim. */
    DUP30("Duplicate claim"),

    /** Member was not enrolled in the plan on the date of service. */
    NOENR("No enrollment"),

    /** The service code submitted is not valid or not covered. */
    INVSVC("Invalid service code"),

    /** Member has reached the coverage limit for this benefit. */
    LIMIT("Coverage limit exceeded");

    private final String memberFacingText;

    DenialReasonCode(String memberFacingText) {
        this.memberFacingText = memberFacingText;
    }

    /**
     * Returns the member-facing explanatory text for this denial reason.
     *
     * @return non-null, non-blank member-facing text
     */
    public String getMemberFacingText() {
        return memberFacingText;
    }

    /**
     * Looks up a {@link DenialReasonCode} by its enum name (the Oracle denial reason code).
     *
     * @param code the denial reason code string, e.g. {@code "TIMELY"} or {@code "DUP30"}
     * @return the matching constant
     * @throws IllegalArgumentException if no constant matches {@code code}
     */
    public static DenialReasonCode fromCode(String code) {
        for (DenialReasonCode d : values()) {
            if (d.name().equals(code)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown denial reason code: " + code);
    }
}
