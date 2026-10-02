package com.claimscore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.claimscore.domain.claim.Claim;

/**
 * Spring Data MongoDB repository for {@link Claim} documents.
 *
 * <p>Provides CRUD operations and domain-specific query methods for the {@code claims} collection.
 */
public interface ClaimRepository extends MongoRepository<Claim, String> {

    /**
     * Finds a claim by its business claim number.
     *
     * @param claimNumber the unique claim number to search for
     * @return an {@link Optional} containing the matching claim, or empty if not found
     */
    Optional<Claim> findByClaimNumber(String claimNumber);

    /**
     * Finds all claims associated with a given member.
     *
     * @param memberId the policy holder identifier
     * @return list of claims for the member, possibly empty
     */
    List<Claim> findByMemberId(Long memberId);

    /**
     * Returns a paginated view of claims in a given processing status.
     *
     * @param status   the status code to filter by
     * @param pageable pagination and sorting parameters
     * @return a page of matching claims
     */
    Page<Claim> findByStatus(String status, Pageable pageable);
}
