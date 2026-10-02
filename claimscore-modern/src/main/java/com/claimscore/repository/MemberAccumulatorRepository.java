package com.claimscore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.claimscore.domain.accumulator.MemberAccumulator;

/**
 * Spring Data MongoDB repository for {@link MemberAccumulator} documents.
 *
 * <p>Provides CRUD operations and domain-specific query methods for the
 * {@code memberAccumulators} collection.
 */
public interface MemberAccumulatorRepository extends MongoRepository<MemberAccumulator, String> {

    /**
     * Finds the accumulator for a specific member, plan, and plan year combination.
     *
     * @param memberId the policy holder identifier
     * @param planId   the plan identifier
     * @param planYear the four-digit plan year
     * @return an {@link Optional} containing the matching accumulator, or empty if not found
     */
    Optional<MemberAccumulator> findByMemberIdAndPlanIdAndPlanYear(
            Long memberId, Long planId, Integer planYear);
}
