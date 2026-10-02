package com.claimscore.domain.provider;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a healthcare provider.
 *
 * <p>Maps to the {@code providers} collection. Consolidates data from Oracle
 * PROVIDERS and PROVIDER_SPECIALTIES tables.
 */
@Document(collection = "providers")
public class Provider {

    /** MongoDB document identifier. */
    @Id
    private String id;

    /** Legacy Oracle provider identifier for migration traceability. */
    private Long legacyId;

    /** National Provider Identifier — unique 10-digit NPI assigned by CMS. */
    private String npi;

    /** Provider's full name or organization name. */
    private String name;

    /** Embedded specialty records for this provider. */
    private List<Specialty> specialties;

    /** Timestamp when this document was first created. */
    private Instant createdAt;

    /** Optimistic locking version field managed by Spring Data MongoDB. */
    @Version
    private Long version;

    /** Creates an empty Provider. */
    public Provider() {}

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

    /** Returns the NPI. */
    public String getNpi() {
        return npi;
    }

    /** Sets the NPI. */
    public void setNpi(String npi) {
        this.npi = npi;
    }

    /** Returns the provider name. */
    public String getName() {
        return name;
    }

    /** Sets the provider name. */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns the embedded specialties. */
    public List<Specialty> getSpecialties() {
        return specialties;
    }

    /** Sets the embedded specialties. */
    public void setSpecialties(List<Specialty> specialties) {
        this.specialties = specialties;
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
