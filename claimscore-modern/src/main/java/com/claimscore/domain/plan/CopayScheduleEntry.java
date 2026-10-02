package com.claimscore.domain.plan;

import java.math.BigDecimal;

/**
 * Embedded document defining a copay amount for a specific place of service.
 *
 * <p>Stored inline within a {@link Plan} document — not a top-level MongoDB collection.
 */
public class CopayScheduleEntry {

    /** Place of service code (e.g. 11 = Office, 21 = Inpatient Hospital). */
    private String placeOfServiceCode;

    /** Fixed copay amount the member owes for services at this place of service. */
    private BigDecimal copayAmt;

    /** Creates an empty CopayScheduleEntry. */
    public CopayScheduleEntry() {}

    /**
     * Creates a CopayScheduleEntry with all fields.
     *
     * @param placeOfServiceCode place of service code
     * @param copayAmt           copay amount
     */
    public CopayScheduleEntry(String placeOfServiceCode, BigDecimal copayAmt) {
        this.placeOfServiceCode = placeOfServiceCode;
        this.copayAmt = copayAmt;
    }

    /** Returns the place of service code. */
    public String getPlaceOfServiceCode() {
        return placeOfServiceCode;
    }

    /** Sets the place of service code. */
    public void setPlaceOfServiceCode(String placeOfServiceCode) {
        this.placeOfServiceCode = placeOfServiceCode;
    }

    /** Returns the copay amount. */
    public BigDecimal getCopayAmt() {
        return copayAmt;
    }

    /** Sets the copay amount. */
    public void setCopayAmt(BigDecimal copayAmt) {
        this.copayAmt = copayAmt;
    }
}
