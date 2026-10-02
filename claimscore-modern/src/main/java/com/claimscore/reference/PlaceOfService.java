package com.claimscore.reference;

/**
 * Enumeration of CMS Place of Service (POS) codes.
 *
 * <p>Replaces the Oracle {@code LK_PLACE_OF_SERVICE} lookup table. Each constant carries the
 * two-digit numeric POS code used on claim forms.
 */
public enum PlaceOfService {

    /** Office — POS code 11. */
    OFFICE("11"),

    /** Inpatient hospital — POS code 21. */
    INPATIENT_HOSPITAL("21"),

    /** Outpatient hospital — POS code 22. */
    OUTPATIENT_HOSPITAL("22"),

    /** Emergency room — POS code 23. */
    EMERGENCY_ROOM("23"),

    /** Home — POS code 12. */
    HOME("12");

    private final String code;

    PlaceOfService(String code) {
        this.code = code;
    }

    /**
     * Returns the two-digit numeric POS code.
     *
     * @return non-null POS code string
     */
    public String getCode() {
        return code;
    }

    /**
     * Looks up a {@link PlaceOfService} by its numeric POS code.
     *
     * @param code the two-digit POS code, e.g. {@code "11"} or {@code "22"}
     * @return the matching constant
     * @throws IllegalArgumentException if no constant matches {@code code}
     */
    public static PlaceOfService fromCode(String code) {
        for (PlaceOfService pos : values()) {
            if (pos.code.equals(code)) {
                return pos;
            }
        }
        throw new IllegalArgumentException("Unknown place of service code: " + code);
    }
}
