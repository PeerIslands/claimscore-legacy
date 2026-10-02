package com.claimscore.reference;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a denial reason reference entry.
 *
 * <p>Maps to the {@code denialReasons} collection. Migrated from the Oracle
 * {@code LK_DENIAL_REASON} table.
 */
@Document(collection = "denialReasons")
public class DenialReason {

    /** MongoDB document identifier. */
    @Id
    private String id;

    /** Denial reason code — Oracle {@code LK_DENIAL_REASON.DENIAL_REASON_CD}. */
    private String code;

    /** Member-facing explanatory text — Oracle {@code LK_DENIAL_REASON.MEMBER_FACING_TEXT}. */
    private String memberFacingText;

    /** Timestamp when this document was first created. */
    private Instant createdAt;

    /** Creates an empty {@link DenialReason}. */
    public DenialReason() {}

    /**
     * Returns the MongoDB document identifier.
     *
     * @return document id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the MongoDB document identifier.
     *
     * @param id document id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the denial reason code.
     *
     * @return denial reason code string
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the denial reason code.
     *
     * @param code denial reason code string
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the member-facing explanatory text.
     *
     * @return member-facing text string
     */
    public String getMemberFacingText() {
        return memberFacingText;
    }

    /**
     * Sets the member-facing explanatory text.
     *
     * @param memberFacingText member-facing text string
     */
    public void setMemberFacingText(String memberFacingText) {
        this.memberFacingText = memberFacingText;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return creation instant
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt creation instant
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
