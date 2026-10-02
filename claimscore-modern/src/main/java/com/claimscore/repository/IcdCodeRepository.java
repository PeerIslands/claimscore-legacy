package com.claimscore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.claimscore.reference.IcdCode;

/**
 * Spring Data MongoDB repository for {@link IcdCode} documents.
 *
 * <p>Provides CRUD operations and domain-specific query methods for the {@code icdCodes}
 * collection. All query methods use Spring Data derived-query naming to eliminate SQL injection
 * risk.
 */
public interface IcdCodeRepository extends MongoRepository<IcdCode, String> {

    /**
     * Finds an ICD code document by its diagnosis code.
     *
     * @param diagnosisCd the ICD diagnosis code to search for
     * @return an {@link Optional} containing the matching document, or empty if not found
     */
    Optional<IcdCode> findByDiagnosisCd(String diagnosisCd);
}
