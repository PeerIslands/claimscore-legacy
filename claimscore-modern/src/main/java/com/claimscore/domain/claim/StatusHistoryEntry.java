package com.claimscore.domain.claim;

import java.time.Instant;

/**
 * Embedded document recording a single status transition on a claim.
 *
 * <p>Stored inline within a {@link Claim} document — not a top-level MongoDB collection.
 */
public class StatusHistoryEntry {

    /** Status code before the transition. */
    private String fromStatus;

    /** Status code after the transition. */
    private String toStatus;

    /** Identifier of the user or system that triggered the change. */
    private String changedBy;

    /** Timestamp when the status change occurred. */
    private Instant changedAt;

    /** Optional reason or comment for the status change. */
    private String reason;

    /** Creates an empty StatusHistoryEntry. */
    public StatusHistoryEntry() {}

    /**
     * Creates a StatusHistoryEntry with all fields.
     *
     * @param fromStatus status before the transition
     * @param toStatus   status after the transition
     * @param changedBy  user or system that triggered the change
     * @param changedAt  timestamp of the change
     * @param reason     optional reason for the change
     */
    public StatusHistoryEntry(
            String fromStatus,
            String toStatus,
            String changedBy,
            Instant changedAt,
            String reason) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
        this.reason = reason;
    }

    /** Returns the status before the transition. */
    public String getFromStatus() {
        return fromStatus;
    }

    /** Sets the status before the transition. */
    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    /** Returns the status after the transition. */
    public String getToStatus() {
        return toStatus;
    }

    /** Sets the status after the transition. */
    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    /** Returns the identifier of who changed the status. */
    public String getChangedBy() {
        return changedBy;
    }

    /** Sets the identifier of who changed the status. */
    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    /** Returns the timestamp of the status change. */
    public Instant getChangedAt() {
        return changedAt;
    }

    /** Sets the timestamp of the status change. */
    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    /** Returns the reason for the status change. */
    public String getReason() {
        return reason;
    }

    /** Sets the reason for the status change. */
    public void setReason(String reason) {
        this.reason = reason;
    }
}
