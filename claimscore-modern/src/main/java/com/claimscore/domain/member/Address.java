package com.claimscore.domain.member;

import java.time.LocalDate;

/**
 * Embedded document representing a mailing or residential address for a policy holder.
 *
 * <p>Stored inline within a {@link PolicyHolder} document — not a top-level MongoDB collection.
 */
public class Address {

    /** Address type code (e.g. HOME, MAILING, BILLING). */
    private String addressType;

    /** First line of the street address. */
    private String line1;

    /** Second line of the street address (suite, apartment, etc.). */
    private String line2;

    /** City name. */
    private String city;

    /** Two-letter state abbreviation. */
    private String state;

    /** ZIP or ZIP+4 postal code. */
    private String zip;

    /** Date this address became effective. */
    private LocalDate effectiveDate;

    /** Date this address was superseded, or null if current. */
    private LocalDate termDate;

    /** Creates an empty Address. */
    public Address() {}

    /**
     * Creates an Address with all fields.
     *
     * @param addressType   address type code
     * @param line1         first address line
     * @param line2         second address line
     * @param city          city name
     * @param state         state abbreviation
     * @param zip           postal code
     * @param effectiveDate date address became effective
     * @param termDate      date address was superseded
     */
    public Address(
            String addressType,
            String line1,
            String line2,
            String city,
            String state,
            String zip,
            LocalDate effectiveDate,
            LocalDate termDate) {
        this.addressType = addressType;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.effectiveDate = effectiveDate;
        this.termDate = termDate;
    }

    /** Returns the address type code. */
    public String getAddressType() {
        return addressType;
    }

    /** Sets the address type code. */
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    /** Returns the first address line. */
    public String getLine1() {
        return line1;
    }

    /** Sets the first address line. */
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    /** Returns the second address line. */
    public String getLine2() {
        return line2;
    }

    /** Sets the second address line. */
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    /** Returns the city. */
    public String getCity() {
        return city;
    }

    /** Sets the city. */
    public void setCity(String city) {
        this.city = city;
    }

    /** Returns the state abbreviation. */
    public String getState() {
        return state;
    }

    /** Sets the state abbreviation. */
    public void setState(String state) {
        this.state = state;
    }

    /** Returns the postal code. */
    public String getZip() {
        return zip;
    }

    /** Sets the postal code. */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /** Returns the effective date. */
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    /** Sets the effective date. */
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /** Returns the termination date. */
    public LocalDate getTermDate() {
        return termDate;
    }

    /** Sets the termination date. */
    public void setTermDate(LocalDate termDate) {
        this.termDate = termDate;
    }
}
