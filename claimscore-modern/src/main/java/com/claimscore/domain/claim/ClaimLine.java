package com.claimscore.domain.claim;

import java.math.BigDecimal;
import java.util.List;

/**
 * Embedded document representing a single service line within a claim.
 *
 * <p>Stored inline within a {@link Claim} document — not a top-level MongoDB collection.
 * Maps to Oracle CLAIM_LINES table rows.
 */
public class ClaimLine {

    /** Sequential line number within the claim. */
    private Integer lineNo;

    /** Service / procedure code (e.g. CPT code). */
    private String svcCd;

    /** Number of units of service rendered. */
    private BigDecimal units;

    /** Amount billed by the provider for this line. */
    private BigDecimal billedAmt;

    /** Allowed amount per the plan contract. */
    private BigDecimal allowedAmt;

    /** Amount paid to the provider for this line. */
    private BigDecimal paidAmt;

    /** Processing status of this individual line. */
    private String lineStatus;

    /** Denial reason code if the line was denied. */
    private String denialReasonCd;

    /** ICD diagnosis codes associated with this service line. */
    private List<String> diagnosisCodes;

    /** Creates an empty ClaimLine. */
    public ClaimLine() {}

    /**
     * Creates a ClaimLine with all fields.
     *
     * @param lineNo         sequential line number
     * @param svcCd          service/procedure code
     * @param units          units of service
     * @param billedAmt      amount billed
     * @param allowedAmt     allowed amount
     * @param paidAmt        amount paid
     * @param lineStatus     line processing status
     * @param denialReasonCd denial reason code
     * @param diagnosisCodes associated diagnosis codes
     */
    public ClaimLine(
            Integer lineNo,
            String svcCd,
            BigDecimal units,
            BigDecimal billedAmt,
            BigDecimal allowedAmt,
            BigDecimal paidAmt,
            String lineStatus,
            String denialReasonCd,
            List<String> diagnosisCodes) {
        this.lineNo = lineNo;
        this.svcCd = svcCd;
        this.units = units;
        this.billedAmt = billedAmt;
        this.allowedAmt = allowedAmt;
        this.paidAmt = paidAmt;
        this.lineStatus = lineStatus;
        this.denialReasonCd = denialReasonCd;
        this.diagnosisCodes = diagnosisCodes;
    }

    /** Returns the line number. */
    public Integer getLineNo() {
        return lineNo;
    }

    /** Sets the line number. */
    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    /** Returns the service code. */
    public String getSvcCd() {
        return svcCd;
    }

    /** Sets the service code. */
    public void setSvcCd(String svcCd) {
        this.svcCd = svcCd;
    }

    /** Returns the units of service. */
    public BigDecimal getUnits() {
        return units;
    }

    /** Sets the units of service. */
    public void setUnits(BigDecimal units) {
        this.units = units;
    }

    /** Returns the billed amount. */
    public BigDecimal getBilledAmt() {
        return billedAmt;
    }

    /** Sets the billed amount. */
    public void setBilledAmt(BigDecimal billedAmt) {
        this.billedAmt = billedAmt;
    }

    /** Returns the allowed amount. */
    public BigDecimal getAllowedAmt() {
        return allowedAmt;
    }

    /** Sets the allowed amount. */
    public void setAllowedAmt(BigDecimal allowedAmt) {
        this.allowedAmt = allowedAmt;
    }

    /** Returns the paid amount. */
    public BigDecimal getPaidAmt() {
        return paidAmt;
    }

    /** Sets the paid amount. */
    public void setPaidAmt(BigDecimal paidAmt) {
        this.paidAmt = paidAmt;
    }

    /** Returns the line status. */
    public String getLineStatus() {
        return lineStatus;
    }

    /** Sets the line status. */
    public void setLineStatus(String lineStatus) {
        this.lineStatus = lineStatus;
    }

    /** Returns the denial reason code. */
    public String getDenialReasonCd() {
        return denialReasonCd;
    }

    /** Sets the denial reason code. */
    public void setDenialReasonCd(String denialReasonCd) {
        this.denialReasonCd = denialReasonCd;
    }

    /** Returns the diagnosis codes. */
    public List<String> getDiagnosisCodes() {
        return diagnosisCodes;
    }

    /** Sets the diagnosis codes. */
    public void setDiagnosisCodes(List<String> diagnosisCodes) {
        this.diagnosisCodes = diagnosisCodes;
    }
}
