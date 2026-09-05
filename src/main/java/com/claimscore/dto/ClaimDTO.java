package com.claimscore.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Claim header plus its lines, carried between the web and EJB tiers.
 *
 * @author rmendez
 * $Id: ClaimDTO.java,v 1.17 2014/01/22 13:40:09 rmendez Exp $
 */
public class ClaimDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long claimId;
    private String claimNumber;
    private Long policyHolderId;
    private String memberNumber;
    private String policyHolderName;
    private Long planId;
    private String planCode;
    private Long providerId;
    private String providerName;
    private String claimTypeCode;
    private String statusCode;
    private String statusName;
    private String placeOfServiceCode;
    private Date incidentDate;
    private Date receivedDate;
    private Date adjudicatedDate;
    private BigDecimal totalBilledAmount;
    private BigDecimal totalAllowedAmount;
    private BigDecimal totalPaidAmount;
    private BigDecimal patientLiabilityAmount;
    private BigDecimal deductibleAppliedAmount;
    private String denialReasonCode;
    private int versionNo;
    private List<ClaimLineDTO> lines = new ArrayList<ClaimLineDTO>();

    public ClaimDTO() {
    }

    public void addLine(ClaimLineDTO line) {
        if (line == null) {
            return;
        }
        lines.add(line);
    }

    public void addLine(int lineNo, String serviceCode, BigDecimal billedAmount) {
        ClaimLineDTO line = new ClaimLineDTO();
        line.setClaimId(claimId);
        line.setLineNumber(lineNo);
        line.setServiceCode(serviceCode);
        line.setBilledAmount(billedAmount);
        line.setUnits(1);
        line.setServiceDate(incidentDate);
        lines.add(line);
    }

    public int getLineCount() {
        return lines.size();
    }

    public List<ClaimLineDTO> getLines() {
        return lines;
    }

    public void setLines(List<ClaimLineDTO> lines) {
        this.lines = lines;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public Long getPolicyHolderId() {
        return policyHolderId;
    }

    public void setPolicyHolderId(Long policyHolderId) {
        this.policyHolderId = policyHolderId;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getPolicyHolderName() {
        return policyHolderName;
    }

    public void setPolicyHolderName(String policyHolderName) {
        this.policyHolderName = policyHolderName;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getClaimTypeCode() {
        return claimTypeCode;
    }

    public void setClaimTypeCode(String claimTypeCode) {
        this.claimTypeCode = claimTypeCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPlaceOfServiceCode() {
        return placeOfServiceCode;
    }

    public void setPlaceOfServiceCode(String placeOfServiceCode) {
        this.placeOfServiceCode = placeOfServiceCode;
    }

    public Date getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(Date incidentDate) {
        this.incidentDate = incidentDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Date getAdjudicatedDate() {
        return adjudicatedDate;
    }

    public void setAdjudicatedDate(Date adjudicatedDate) {
        this.adjudicatedDate = adjudicatedDate;
    }

    public BigDecimal getTotalBilledAmount() {
        return totalBilledAmount;
    }

    public void setTotalBilledAmount(BigDecimal totalBilledAmount) {
        this.totalBilledAmount = totalBilledAmount;
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

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }
}
