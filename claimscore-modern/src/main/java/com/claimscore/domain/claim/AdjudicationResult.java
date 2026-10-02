package com.claimscore.domain.claim;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Embedded document capturing the outcome of claim adjudication.
 *
 * <p>Stored inline within a {@link Claim} document — not a top-level MongoDB collection.
 */
public class AdjudicationResult {

    /** Result code produced by the adjudication engine. */
    private String resultCode;

    /** Human-readable adjudication message or denial reason. */
    private String message;

    /** Deductible amount applied during adjudication. */
    private BigDecimal deductibleApplied;

    /** Copay amount owed by the member. */
    private BigDecimal copayAmt;

    /** Coinsurance amount owed by the member. */
    private BigDecimal coinsuranceAmt;

    /** Total patient liability computed by adjudication. */
    private BigDecimal patientLiability;

    /** Identifier of the user or system that performed adjudication. */
    private String adjudicatedBy;

    /** Timestamp when adjudication was completed. */
    private Instant adjudicatedAt;

    /** Creates an empty AdjudicationResult. */
    public AdjudicationResult() {}

    /**
     * Creates an AdjudicationResult with all fields.
     *
     * @param resultCode       result code from the adjudication engine
     * @param message          human-readable message
     * @param deductibleApplied deductible amount applied
     * @param copayAmt         copay amount
     * @param coinsuranceAmt   coinsurance amount
     * @param patientLiability total patient liability
     * @param adjudicatedBy    user or system that adjudicated
     * @param adjudicatedAt    timestamp of adjudication
     */
    public AdjudicationResult(
            String resultCode,
            String message,
            BigDecimal deductibleApplied,
            BigDecimal copayAmt,
            BigDecimal coinsuranceAmt,
            BigDecimal patientLiability,
            String adjudicatedBy,
            Instant adjudicatedAt) {
        this.resultCode = resultCode;
        this.message = message;
        this.deductibleApplied = deductibleApplied;
        this.copayAmt = copayAmt;
        this.coinsuranceAmt = coinsuranceAmt;
        this.patientLiability = patientLiability;
        this.adjudicatedBy = adjudicatedBy;
        this.adjudicatedAt = adjudicatedAt;
    }

    /** Returns the result code. */
    public String getResultCode() {
        return resultCode;
    }

    /** Sets the result code. */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /** Returns the adjudication message. */
    public String getMessage() {
        return message;
    }

    /** Sets the adjudication message. */
    public void setMessage(String message) {
        this.message = message;
    }

    /** Returns the deductible amount applied. */
    public BigDecimal getDeductibleApplied() {
        return deductibleApplied;
    }

    /** Sets the deductible amount applied. */
    public void setDeductibleApplied(BigDecimal deductibleApplied) {
        this.deductibleApplied = deductibleApplied;
    }

    /** Returns the copay amount. */
    public BigDecimal getCopayAmt() {
        return copayAmt;
    }

    /** Sets the copay amount. */
    public void setCopayAmt(BigDecimal copayAmt) {
        this.copayAmt = copayAmt;
    }

    /** Returns the coinsurance amount. */
    public BigDecimal getCoinsuranceAmt() {
        return coinsuranceAmt;
    }

    /** Sets the coinsurance amount. */
    public void setCoinsuranceAmt(BigDecimal coinsuranceAmt) {
        this.coinsuranceAmt = coinsuranceAmt;
    }

    /** Returns the total patient liability. */
    public BigDecimal getPatientLiability() {
        return patientLiability;
    }

    /** Sets the total patient liability. */
    public void setPatientLiability(BigDecimal patientLiability) {
        this.patientLiability = patientLiability;
    }

    /** Returns the identifier of who adjudicated. */
    public String getAdjudicatedBy() {
        return adjudicatedBy;
    }

    /** Sets the identifier of who adjudicated. */
    public void setAdjudicatedBy(String adjudicatedBy) {
        this.adjudicatedBy = adjudicatedBy;
    }

    /** Returns the adjudication timestamp. */
    public Instant getAdjudicatedAt() {
        return adjudicatedAt;
    }

    /** Sets the adjudication timestamp. */
    public void setAdjudicatedAt(Instant adjudicatedAt) {
        this.adjudicatedAt = adjudicatedAt;
    }
}
