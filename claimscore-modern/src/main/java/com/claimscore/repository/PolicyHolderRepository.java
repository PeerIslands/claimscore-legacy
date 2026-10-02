package com.claimscore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.claimscore.domain.member.PolicyHolder;

/**
 * Spring Data MongoDB repository for {@link PolicyHolder} documents.
 *
 * <p>Provides CRUD operations and domain-specific query methods for the
 * {@code policyHolders} collection.
 */
public interface PolicyHolderRepository extends MongoRepository<PolicyHolder, String> {

    /**
     * Finds a policy holder by their business member number.
     *
     * @param memberNumber the unique member number to search for
     * @return an {@link Optional} containing the matching policy holder, or empty if not found
     */
    Optional<PolicyHolder> findByMemberNumber(String memberNumber);
}
