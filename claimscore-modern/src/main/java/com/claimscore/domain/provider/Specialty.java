package com.claimscore.domain.provider;

/**
 * Embedded document representing a provider's medical specialty.
 *
 * <p>Stored inline within a {@link Provider} document — not a top-level MongoDB collection.
 */
public class Specialty {

    /** Specialty classification code (e.g. NUCC taxonomy code). */
    private String specialtyCode;

    /** Whether this is the provider's primary specialty. */
    private Boolean isPrimary;

    /** Creates an empty Specialty. */
    public Specialty() {}

    /**
     * Creates a Specialty with all fields.
     *
     * @param specialtyCode specialty classification code
     * @param isPrimary     whether this is the primary specialty
     */
    public Specialty(String specialtyCode, Boolean isPrimary) {
        this.specialtyCode = specialtyCode;
        this.isPrimary = isPrimary;
    }

    /** Returns the specialty code. */
    public String getSpecialtyCode() {
        return specialtyCode;
    }

    /** Sets the specialty code. */
    public void setSpecialtyCode(String specialtyCode) {
        this.specialtyCode = specialtyCode;
    }

    /** Returns whether this is the primary specialty. */
    public Boolean getIsPrimary() {
        return isPrimary;
    }

    /** Sets whether this is the primary specialty. */
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
