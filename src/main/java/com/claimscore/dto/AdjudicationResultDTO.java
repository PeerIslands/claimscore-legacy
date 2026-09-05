package com.claimscore.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Outcome of PKG_CLAIMS.ADJUDICATE_CLAIM as returned by the adjudication EJB.
 *
 * @author rmendez
 * $Id: AdjudicationResultDTO.java,v 1.5 2010/06/30 12:48:57 rmendez Exp $
 */
public class AdjudicationResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long claimId;
    private String resultCode;
    private String message;
    private BigDecimal totalAllowedAmount;
    private BigDecimal totalPaidAmount;
    private BigDecimal patientLiabilityAmount;
    private BigDecimal deductibleAppliedAmount;
    private String denialReasonCode;
    private Date adjudicatedDate;

    public AdjudicationResultDTO() {
    }

    public boolean isApproved() {
        return "APR".equals(resultCode) || "PAR".equals(resultCode);
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getTotalAllowedAmount() {
        return totalAllowedAmount;
    }

    public void setTotalAllowedAmount(BigDecimal totalAllowedAmount) {
        this.totalAllowedAmount = totalAllowedAmount;
    }

    public BigDecimal getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(BigDecimal totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public BigDecimal getPatientLiabilityAmount() {
        return patientLiabilityAmount;
    }

    public void setPatientLiabilityAmount(BigDecimal patientLiabilityAmount) {
        this.patientLiabilityAmount = patientLiabilityAmount;
    }

    public BigDecimal getDeductibleAppliedAmount() {
        return deductibleAppliedAmount;
    }

    public void setDeductibleAppliedAmount(BigDecimal deductibleAppliedAmount) {
        this.deductibleAppliedAmount = deductibleAppliedAmount;
    }

    public String getDenialReasonCode() {
        return denialReasonCode;
    }

    public void setDenialReasonCode(String denialReasonCode) {
        this.denialReasonCode = denialReasonCode;
    }

    public Date getAdjudicatedDate() {
        return adjudicatedDate;
    }

    public void setAdjudicatedDate(Date adjudicatedDate) {
        this.adjudicatedDate = adjudicatedDate;
    }
}
