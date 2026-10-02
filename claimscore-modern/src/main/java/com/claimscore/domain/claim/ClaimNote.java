package com.claimscore.domain.claim;

import java.time.Instant;

/**
 * Embedded document representing a free-text note attached to a claim.
 *
 * <p>Stored inline within a {@link Claim} document — not a top-level MongoDB collection.
 */
public class ClaimNote {

    /** Code classifying the type of note (e.g. EXAMINER, SYSTEM, AUDIT). */
    private String noteTypeCode;

    /** Free-text content of the note. */
    private String noteText;

    /** Identifier of the user or system that created the note. */
    private String createdBy;

    /** Timestamp when the note was created. */
    private Instant createdAt;

    /** Creates an empty ClaimNote. */
    public ClaimNote() {}

    /**
     * Creates a ClaimNote with all fields.
     *
     * @param noteTypeCode type code for the note
     * @param noteText     free-text note content
     * @param createdBy    author identifier
     * @param createdAt    creation timestamp
     */
    public ClaimNote(String noteTypeCode, String noteText, String createdBy, Instant createdAt) {
        this.noteTypeCode = noteTypeCode;
        this.noteText = noteText;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    /** Returns the note type code. */
    public String getNoteTypeCode() {
        return noteTypeCode;
    }

    /** Sets the note type code. */
    public void setNoteTypeCode(String noteTypeCode) {
        this.noteTypeCode = noteTypeCode;
    }

    /** Returns the note text. */
    public String getNoteText() {
        return noteText;
    }

    /** Sets the note text. */
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    /** Returns the creator identifier. */
    public String getCreatedBy() {
        return createdBy;
    }

    /** Sets the creator identifier. */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /** Returns the creation timestamp. */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** Sets the creation timestamp. */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
