package com.claimscore.domain.member;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a policy holder (member).
 *
 * <p>Maps to the {@code policyHolders} collection. Consolidates data from Oracle
 * POLICY_HOLDERS, ENROLLMENTS, and MEMBER_ADDRESSES tables.
 */
@Document(collection = "policyHolders")
public class PolicyHolder {

    /** MongoDB document identifier. */
    @Id
    private String id;

    /** Legacy Oracle POLICY_HOLDERS.PH_ID for migration traceability. */
    private Long legacyId;

    /** Business member number — Oracle POLICY_HOLDERS.MEMBER_NUM. */
    private String memberNumber;

    /** Member's given name. */
    private String firstName;

    /** Member's family name. */
    private String lastName;

    /** Member's date of birth. */
    private LocalDate dateOfBirth;

    /** Last four digits of the member's SSN. NEVER log this field. */
    private String ssnLast4;

    /** Embedded plan enrollment records. */
    private List<Enrollment> enrollments;

    /** Embedded address records. */
    private List<Address> addresses;

    /** Timestamp when this document was first created. */
    private Instant createdAt;

    /** Timestamp when this document was last modified. */
    private Instant updatedAt;

    /** Optimistic locking version field managed by Spring Data MongoDB. */
    @Version
    private Long version;

    /** Creates an empty PolicyHolder. */
    public PolicyHolder() {}

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

    /** Returns the business member number. */
    public String getMemberNumber() {
        return memberNumber;
    }

    /** Sets the business member number. */
    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    /** Returns the first name. */
    public String getFirstName() {
        return firstName;
    }

    /** Sets the first name. */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** Returns the last name. */
    public String getLastName() {
        return lastName;
    }

    /** Sets the last name. */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** Returns the date of birth. */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /** Sets the date of birth. */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /** Returns the last four digits of the SSN. NEVER log this field. */
    public String getSsnLast4() {
        return ssnLast4;
    }

    /** Sets the last four digits of the SSN. NEVER log this field. */
    public void setSsnLast4(String ssnLast4) {
        this.ssnLast4 = ssnLast4;
    }

    /** Returns the embedded enrollments. */
    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    /** Sets the embedded enrollments. */
    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    /** Returns the embedded addresses. */
    public List<Address> getAddresses() {
        return addresses;
    }

    /** Sets the embedded addresses. */
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
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
