package com.claimscore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.claimscore.reference.DenialReason;

/**
 * Spring Data MongoDB repository for {@link DenialReason} documents.
 *
 * <p>Provides CRUD operations and domain-specific query methods for the {@code denialReasons}
 * collection. All query methods use Spring Data derived-query naming to eliminate SQL injection
 * risk.
 */
public interface DenialReasonRepository extends MongoRepository<DenialReason, String> {

    /**
     * Finds a denial reason document by its denial reason code.
     *
     * @param code the denial reason code to search for
     * @return an {@link Optional} containing the matching document, or empty if not found
     */
    Optional<DenialReason> findByCode(String code);
}
