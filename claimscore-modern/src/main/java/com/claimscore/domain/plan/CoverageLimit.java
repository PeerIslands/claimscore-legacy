package com.claimscore.domain.plan;

import java.math.BigDecimal;

/**
 * Embedded document defining a coverage limit for a specific service category.
 *
 * <p>Stored inline within a {@link Plan} document — not a top-level MongoDB collection.
 */
public class CoverageLimit {

    /** Service category code this limit applies to (e.g. PHYS_THERAPY, DME). */
    private String categoryCode;

    /** Maximum allowed amount per individual claim for this category. */
    private BigDecimal perClaimLimitAmt;

    /** Maximum allowed amount across all claims in a plan year for this category. */
    private BigDecimal annualLimitAmt;

    /** Creates an empty CoverageLimit. */
    public CoverageLimit() {}

    /**
     * Creates a CoverageLimit with all fields.
     *
     * @param categoryCode      service category code
     * @param perClaimLimitAmt  per-claim limit amount
     * @param annualLimitAmt    annual limit amount
     */
    public CoverageLimit(
            String categoryCode, BigDecimal perClaimLimitAmt, BigDecimal annualLimitAmt) {
        this.categoryCode = categoryCode;
        this.perClaimLimitAmt = perClaimLimitAmt;
        this.annualLimitAmt = annualLimitAmt;
    }

    /** Returns the category code. */
    public String getCategoryCode() {
        return categoryCode;
    }

    /** Sets the category code. */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    /** Returns the per-claim limit amount. */
    public BigDecimal getPerClaimLimitAmt() {
        return perClaimLimitAmt;
    }

    /** Sets the per-claim limit amount. */
    public void setPerClaimLimitAmt(BigDecimal perClaimLimitAmt) {
        this.perClaimLimitAmt = perClaimLimitAmt;
    }

    /** Returns the annual limit amount. */
    public BigDecimal getAnnualLimitAmt() {
        return annualLimitAmt;
    }

    /** Sets the annual limit amount. */
    public void setAnnualLimitAmt(BigDecimal annualLimitAmt) {
        this.annualLimitAmt = annualLimitAmt;
    }
}
