package com.claimscore.domain.claim;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a healthcare claim.
 *
 * <p>Maps to the {@code claims} collection. Consolidates data from 47 Oracle tables including
 * CLAIMS, CLAIM_LINES, CLAIM_NOTES, CLAIM_STATUS_HISTORY, and ADJUDICATION_RESULTS.
 */
@Document(collection = "claims")
public class Claim {

    /** MongoDB document identifier. */
    @Id
    private String id;

    /** Legacy Oracle CLAIMS.CLAIM_ID for migration traceability. */
    private Long legacyId;

    /** Business claim number — Oracle CLAIMS.CLAIM_NUM. */
    private String claimNumber;

    /** Policy holder identifier — Oracle CLAIMS.PH_ID. */
    private Long memberId;

    /** Rendering provider identifier — Oracle CLAIMS.PROVIDER_ID. */
    private Long providerId;

    /** Plan identifier — Oracle CLAIMS.PLAN_ID. */
    private Long planId;

    /** Current processing status code — Oracle CLAIMS.STATUS_CD. */
    private String status;

    /** Date the covered incident or service occurred — Oracle CLAIMS.INCIDENT_DT. */
    private LocalDate incidentDate;

    /** Date the claim was received — Oracle CLAIMS.RECEIVED_DT. */
    private LocalDate receivedDate;

    /** Date the claim was adjudicated. */
    private LocalDate adjudicatedDate;

    /** Total amount billed by the provider across all lines. */
    private BigDecimal totalBilledAmt;

    /** Total allowed amount across all lines per plan contract. */
    private BigDecimal totalAllowedAmt;

    /** Total amount paid to the provider. */
    private BigDecimal totalPaidAmt;

    /** Total patient liability (deductible + copay + coinsurance). */
    private BigDecimal totalPatientLiabilityAmt;

    /** Embedded service lines for this claim. */
    private List<ClaimLine> lines;

    /** Embedded examiner and system notes. */
    private List<ClaimNote> notes;

    /** Embedded audit trail of status transitions. */
    private List<StatusHistoryEntry> statusHistory;

    /** Embedded adjudication outcome. */
    private AdjudicationResult adjudicationResult;

    /** Timestamp when this document was first created. */
    private Instant createdAt;

    /** Timestamp when this document was last modified. */
    private Instant updatedAt;

    /** Optimistic locking version field managed by Spring Data MongoDB. */
    @Version
    private Long version;

    /** Creates an empty Claim. */
    public Claim() {}

    /** Returns the MongoDB document identifier. */
    public String getId() {
        return id;
    }

    /** Sets the MongoDB document identifier. */
    public void setId(String id) {
        this.id = id;
    }

    /** Returns the legacy Oracle claim identifier. */
    public Long getLegacyId() {
        return legacyId;
    }

    /** Sets the legacy Oracle claim identifier. */
    public void setLegacyId(Long legacyId) {
        this.legacyId = legacyId;
    }

    /** Returns the business claim number. */
    public String getClaimNumber() {
        return claimNumber;
    }

    /** Sets the business claim number. */
    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    /** Returns the member identifier. */
    public Long getMemberId() {
        return memberId;
    }

    /** Sets the member identifier. */
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    /** Returns the provider identifier. */
    public Long getProviderId() {
        return providerId;
    }

    /** Sets the provider identifier. */
    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    /** Returns the plan identifier. */
    public Long getPlanId() {
        return planId;
    }

    /** Sets the plan identifier. */
    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    /** Returns the current status code. */
    public String getStatus() {
        return status;
    }

    /** Sets the current status code. */
    public void setStatus(String status) {
        this.status = status;
    }

    /** Returns the incident date. */
    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    /** Sets the incident date. */
    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    /** Returns the received date. */
    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    /** Sets the received date. */
    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    /** Returns the adjudicated date. */
    public LocalDate getAdjudicatedDate() {
        return adjudicatedDate;
    }

    /** Sets the adjudicated date. */
    public void setAdjudicatedDate(LocalDate adjudicatedDate) {
        this.adjudicatedDate = adjudicatedDate;
    }

    /** Returns the total billed amount. */
    public BigDecimal getTotalBilledAmt() {
        return totalBilledAmt;
    }

    /** Sets the total billed amount. */
    public void setTotalBilledAmt(BigDecimal totalBilledAmt) {
        this.totalBilledAmt = totalBilledAmt;
    }

    /** Returns the total allowed amount. */
    public BigDecimal getTotalAllowedAmt() {
        return totalAllowedAmt;
    }

    /** Sets the total allowed amount. */
    public void setTotalAllowedAmt(BigDecimal totalAllowedAmt) {
        this.totalAllowedAmt = totalAllowedAmt;
    }

    /** Returns the total paid amount. */
    public BigDecimal getTotalPaidAmt() {
        return totalPaidAmt;
    }

    /** Sets the total paid amount. */
    public void setTotalPaidAmt(BigDecimal totalPaidAmt) {
        this.totalPaidAmt = totalPaidAmt;
    }

    /** Returns the total patient liability amount. */
    public BigDecimal getTotalPatientLiabilityAmt() {
        return totalPatientLiabilityAmt;
    }

    /** Sets the total patient liability amount. */
    public void setTotalPatientLiabilityAmt(BigDecimal totalPatientLiabilityAmt) {
        this.totalPatientLiabilityAmt = totalPatientLiabilityAmt;
    }

    /** Returns the embedded claim lines. */
    public List<ClaimLine> getLines() {
        return lines;
    }

    /** Sets the embedded claim lines. */
    public void setLines(List<ClaimLine> lines) {
        this.lines = lines;
    }

    /** Returns the embedded claim notes. */
    public List<ClaimNote> getNotes() {
        return notes;
    }

    /** Sets the embedded claim notes. */
    public void setNotes(List<ClaimNote> notes) {
        this.notes = notes;
    }

    /** Returns the embedded status history. */
    public List<StatusHistoryEntry> getStatusHistory() {
        return statusHistory;
    }

    /** Sets the embedded status history. */
    public void setStatusHistory(List<StatusHistoryEntry> statusHistory) {
        this.statusHistory = statusHistory;
    }

    /** Returns the embedded adjudication result. */
    public AdjudicationResult getAdjudicationResult() {
        return adjudicationResult;
    }

    /** Sets the embedded adjudication result. */
    public void setAdjudicationResult(AdjudicationResult adjudicationResult) {
        this.adjudicationResult = adjudicationResult;
    }

    /** Returns the creation timestamp. */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** Sets the creation timestamp. */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /** Returns the last-modified timestamp. */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /** Sets the last-modified timestamp. */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** Returns the optimistic locking version. */
    public Long getVersion() {
        return version;
    }

    /** Sets the optimistic locking version. */
    public void setVersion(Long version) {
        this.version = version;
    }
}
