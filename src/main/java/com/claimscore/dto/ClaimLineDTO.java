package com.claimscore.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * One service line of a claim (CLAIM_LINES).
 *
 * @author rmendez
 * $Id: ClaimLineDTO.java,v 1.4 2009/03/11 10:22:51 rmendez Exp $
 */
public class ClaimLineDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long lineId;
    private Long claimId;
    private int lineNumber;
    private String serviceCode;
    private String diagnosisCode;
    private Date serviceDate;
    private int units;
    private BigDecimal billedAmount;
    private BigDecimal allowedAmount;
    private BigDecimal paidAmount;
    private String lineStatusCode;
    private String denialReasonCode;

    public ClaimLineDTO() {
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public BigDecimal getAllowedAmount() {
        return allowedAmount;
    }

    public void setAllowedAmount(BigDecimal allowedAmount) {
        this.allowedAmount = allowedAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getLineStatusCode() {
        return lineStatusCode;
    }

    public void setLineStatusCode(String lineStatusCode) {
        this.lineStatusCode = lineStatusCode;
    }

    public String getDenialReasonCode() {
        return denialReasonCode;
    }

    public void setDenialReasonCode(String denialReasonCode) {
        this.denialReasonCode = denialReasonCode;
    }
}
