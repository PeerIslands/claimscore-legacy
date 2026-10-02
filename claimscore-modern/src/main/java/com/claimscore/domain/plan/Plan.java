package com.claimscore.domain.plan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a health insurance plan.
 *
 * <p>Maps to the {@code plans} collection. Consolidates data from Oracle PLANS,
 * PLAN_COVERAGE_LIMITS, and PLAN_COPAY_SCHEDULES tables.
 *
 * <p>{@code planYearStartMonth} is critical for the DEFECT-001 fix — it determines the
 * month boundary used when computing year-to-date accumulator resets.
 */
@Document(collection = "plans")
public class Plan {

    /** MongoDB document identifier. */
    @Id
    private String id;

    /** Legacy Oracle plan identifier for migration traceability. */
    private Long legacyId;

    /** Human-readable plan name. */
    private String planName;

    /** Annual deductible amount a member must meet before plan benefits apply. */
    private BigDecimal annualDeductibleAmt;

    /** Annual out-of-pocket maximum amount after which the plan pays 100%. */
    private BigDecimal oopMaxAmt;

    /** Coinsurance percentage (0–100) the member pays after the deductible. */
    private BigDecimal coinsurancePct;

    /**
     * Calendar month (1 = January, 7 = July, etc.) when the plan year begins.
     *
     * <p>CRITICAL for DEFECT-001 fix: accumulator reset logic must use this field
     * rather than assuming a January plan year start.
     */
    private Integer planYearStartMonth;

    /** Embedded coverage limits by service category. */
    private List<CoverageLimit> coverageLimits;

    /** Embedded copay schedule by place of service. */
    private List<CopayScheduleEntry> copaySchedule;

    /** Timestamp when this document was first created. */
    private Instant createdAt;

    /** Optimistic locking version field managed by Spring Data MongoDB. */
    @Version
    private Long version;

    /** Creates an empty Plan. */
    public Plan() {}

    /** Returns the MongoDB document identifier. */
    public String getId() {
        return id;
    }

    /** Sets the MongoDB document identifier. */
    public void setId(String id) {
        this.id = id;
    }

    /** Returns the legacy Oracle identifier. */
    public Long getLegacyId() {
        return legacyId;
    }

    /** Sets the legacy Oracle identifier. */
    public void setLegacyId(Long legacyId) {
        this.legacyId = legacyId;
    }

    /** Returns the plan name. */
    public String getPlanName() {
        return planName;
    }

    /** Sets the plan name. */
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    /** Returns the annual deductible amount. */
    public BigDecimal getAnnualDeductibleAmt() {
        return annualDeductibleAmt;
    }

    /** Sets the annual deductible amount. */
    public void setAnnualDeductibleAmt(BigDecimal annualDeductibleAmt) {
        this.annualDeductibleAmt = annualDeductibleAmt;
    }

    /** Returns the out-of-pocket maximum amount. */
    public BigDecimal getOopMaxAmt() {
        return oopMaxAmt;
    }

    /** Sets the out-of-pocket maximum amount. */
    public void setOopMaxAmt(BigDecimal oopMaxAmt) {
        this.oopMaxAmt = oopMaxAmt;
    }

    /** Returns the coinsurance percentage. */
    public BigDecimal getCoinsurancePct() {
        return coinsurancePct;
    }

    /** Sets the coinsurance percentage. */
    public void setCoinsurancePct(BigDecimal coinsurancePct) {
        this.coinsurancePct = coinsurancePct;
    }

    /**
     * Returns the calendar month when the plan year begins (1 = January).
     *
     * <p>CRITICAL for DEFECT-001 fix.
     */
    public Integer getPlanYearStartMonth() {
        return planYearStartMonth;
    }

    /**
     * Sets the calendar month when the plan year begins (1 = January).
     *
     * <p>CRITICAL for DEFECT-001 fix.
     */
    public void setPlanYearStartMonth(Integer planYearStartMonth) {
        this.planYearStartMonth = planYearStartMonth;
    }

    /** Returns the embedded coverage limits. */
    public List<CoverageLimit> getCoverageLimits() {
        return coverageLimits;
    }

    /** Sets the embedded coverage limits. */
    public void setCoverageLimits(List<CoverageLimit> coverageLimits) {
        this.coverageLimits = coverageLimits;
    }

    /** Returns the embedded copay schedule. */
    public List<CopayScheduleEntry> getCopaySchedule() {
        return copaySchedule;
    }

    /** Sets the embedded copay schedule. */
    public void setCopaySchedule(List<CopayScheduleEntry> copaySchedule) {
        this.copaySchedule = copaySchedule;
    }

    /** Returns the creation timestamp. */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** Sets the creation timestamp. */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
