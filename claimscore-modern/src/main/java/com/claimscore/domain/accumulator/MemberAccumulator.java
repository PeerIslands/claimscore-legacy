package com.claimscore.domain.accumulator;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document tracking a member's year-to-date accumulator balances.
 *
 * <p>Maps to the {@code memberAccumulators} collection. Consolidates deductible and
 * out-of-pocket accumulation data from Oracle MEMBER_ACCUMULATORS and related tables.
 */
@Document(collection = "memberAccumulators")
public class MemberAccumulator {

    /** MongoDB document identifier. */
    @Id
    private String id;

    /** Policy holder identifier this accumulator belongs to. */
    private Long memberId;

    /** Plan identifier this accumulator is scoped to. */
    private Long planId;

    /** Calendar year this accumulator covers. */
    private Integer planYear;

    /** Year-to-date deductible amount met by the member. */
    private BigDecimal deductibleMet;

    /** Year-to-date out-of-pocket maximum amount met by the member. */
    private BigDecimal oopMet;

    /** Timestamp of the most recent update to this accumulator. */
    private Instant lastUpdated;

    /** Optimistic locking version field managed by Spring Data MongoDB. */
    @Version
    private Long version;

    /** Creates an empty MemberAccumulator. */
    public MemberAccumulator() {}

    /** Returns the MongoDB document identifier. */
    public String getId() {
        return id;
    }

    /** Sets the MongoDB document identifier. */
    public void setId(String id) {
        this.id = id;
    }

    /** Returns the member identifier. */
    public Long getMemberId() {
        return memberId;
    }

    /** Sets the member identifier. */
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    /** Returns the plan identifier. */
    public Long getPlanId() {
        return planId;
    }

    /** Sets the plan identifier. */
    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    /** Returns the plan year. */
    public Integer getPlanYear() {
        return planYear;
    }

    /** Sets the plan year. */
    public void setPlanYear(Integer planYear) {
        this.planYear = planYear;
    }

    /** Returns the year-to-date deductible met. */
    public BigDecimal getDeductibleMet() {
        return deductibleMet;
    }

    /** Sets the year-to-date deductible met. */
    public void setDeductibleMet(BigDecimal deductibleMet) {
        this.deductibleMet = deductibleMet;
    }

    /** Returns the year-to-date out-of-pocket met. */
    public BigDecimal getOopMet() {
        return oopMet;
    }

    /** Sets the year-to-date out-of-pocket met. */
    public void setOopMet(BigDecimal oopMet) {
        this.oopMet = oopMet;
    }

    /** Returns the last-updated timestamp. */
    public Instant getLastUpdated() {
        return lastUpdated;
    }

    /** Sets the last-updated timestamp. */
    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
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
