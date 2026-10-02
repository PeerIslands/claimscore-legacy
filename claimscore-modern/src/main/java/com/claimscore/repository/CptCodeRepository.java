package com.claimscore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.claimscore.reference.CptCode;

/**
 * Spring Data MongoDB repository for {@link CptCode} documents.
 *
 * <p>Provides CRUD operations and domain-specific query methods for the {@code cptCodes}
 * collection. All query methods use Spring Data derived-query naming to eliminate SQL injection
 * risk.
 */
public interface CptCodeRepository extends MongoRepository<CptCode, String> {

    /**
     * Finds a CPT code document by its service code.
     *
     * @param svcCd the CPT service code to search for
     * @return an {@link Optional} containing the matching document, or empty if not found
     */
    Optional<CptCode> findBySvcCd(String svcCd);

    /**
     * Finds all CPT code documents belonging to a given category.
     *
     * @param categoryCode the category code to filter by
     * @return list of matching CPT code documents, possibly empty
     */
    List<CptCode> findByCategoryCode(String categoryCode);
}
