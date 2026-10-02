package com.claimscore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.claimscore.domain.plan.Plan;

/**
 * Spring Data MongoDB repository for {@link Plan} documents.
 *
 * <p>Provides CRUD operations and domain-specific query methods for the {@code plans} collection.
 */
public interface PlanRepository extends MongoRepository<Plan, String> {

    /**
     * Finds a plan by its legacy Oracle plan identifier.
     *
     * @param legacyId the Oracle plan identifier
     * @return an {@link Optional} containing the matching plan, or empty if not found
     */
    Optional<Plan> findByLegacyId(Long legacyId);
}
