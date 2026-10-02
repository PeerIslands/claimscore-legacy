package com.claimscore.reference;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing an ICD (International Classification of Diseases) diagnosis code.
 *
 * <p>Maps to the {@code icdCodes} collection. Migrated from the Oracle {@code ICD_CODES} table.
 */
@Document(collection = "icdCodes")
public class IcdCode {

    /** MongoDB document identifier. */
    @Id
    private String id;

    /** Diagnosis code — Oracle {@code ICD_CODES.DIAGNOSIS_CD}. */
    private String diagnosisCd;

    /** Human-readable description of the diagnosis. */
    private String description;

    /** Timestamp when this document was first created. */
    private Instant createdAt;

    /** Creates an empty {@link IcdCode}. */
    public IcdCode() {}

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
     * Returns the ICD diagnosis code.
     *
     * @return diagnosis code string
     */
    public String getDiagnosisCd() {
        return diagnosisCd;
    }

    /**
     * Sets the ICD diagnosis code.
     *
     * @param diagnosisCd diagnosis code string
     */
    public void setDiagnosisCd(String diagnosisCd) {
        this.diagnosisCd = diagnosisCd;
    }

    /**
     * Returns the diagnosis description.
     *
     * @return description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the diagnosis description.
     *
     * @param description description string
     */
    public void setDescription(String description) {
        this.description = description;
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
