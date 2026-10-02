package com.claimscore.reference;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a CPT (Current Procedural Terminology) code.
 *
 * <p>Maps to the {@code cptCodes} collection. Migrated from the Oracle {@code CPT_CODES} table.
 */
@Document(collection = "cptCodes")
public class CptCode {

    /** MongoDB document identifier. */
    @Id
    private String id;

    /** Service code — Oracle {@code CPT_CODES.SVC_CD}. */
    private String svcCd;

    /** Human-readable description of the procedure. */
    private String description;

    /** Category code — Oracle {@code CPT_CODES.CATEGORY_CD}. */
    private String categoryCode;

    /** Timestamp when this document was first created. */
    private Instant createdAt;

    /** Creates an empty {@link CptCode}. */
    public CptCode() {}

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
     * Returns the CPT service code.
     *
     * @return service code string
     */
    public String getSvcCd() {
        return svcCd;
    }

    /**
     * Sets the CPT service code.
     *
     * @param svcCd service code string
     */
    public void setSvcCd(String svcCd) {
        this.svcCd = svcCd;
    }

    /**
     * Returns the procedure description.
     *
     * @return description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the procedure description.
     *
     * @param description description string
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the category code.
     *
     * @return category code string
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * Sets the category code.
     *
     * @param categoryCode category code string
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
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
