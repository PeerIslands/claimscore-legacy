package com.claimscore.domain.member;

import java.time.LocalDate;

/**
 * Embedded document representing a member's enrollment in a plan.
 *
 * <p>Stored inline within a {@link PolicyHolder} document — not a top-level MongoDB collection.
 */
public class Enrollment {

    /** Identifier of the plan the member is enrolled in. */
    private Long planId;

    /** Date the enrollment became effective. */
    private LocalDate effectiveDate;

    /** Date the enrollment was terminated, or null if still active. */
    private LocalDate termDate;

    /** Enrollment status code (e.g. ACTIVE, TERMED, PENDING). */
    private String status;

    /** Creates an empty Enrollment. */
    public Enrollment() {}

    /**
     * Creates an Enrollment with all fields.
     *
     * @param planId        plan identifier
     * @param effectiveDate enrollment effective date
     * @param termDate      enrollment termination date
     * @param status        enrollment status code
     */
    public Enrollment(Long planId, LocalDate effectiveDate, LocalDate termDate, String status) {
        this.planId = planId;
        this.effectiveDate = effectiveDate;
        this.termDate = termDate;
        this.status = status;
    }

    /** Returns the plan identifier. */
    public Long getPlanId() {
        return planId;
    }

    /** Sets the plan identifier. */
    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    /** Returns the effective date. */
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    /** Sets the effective date. */
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /** Returns the termination date. */
    public LocalDate getTermDate() {
        return termDate;
    }

    /** Sets the termination date. */
    public void setTermDate(LocalDate termDate) {
        this.termDate = termDate;
    }

    /** Returns the enrollment status. */
    public String getStatus() {
        return status;
    }

    /** Sets the enrollment status. */
    public void setStatus(String status) {
        this.status = status;
    }
}
