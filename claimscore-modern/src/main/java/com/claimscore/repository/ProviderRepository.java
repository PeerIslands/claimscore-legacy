package com.claimscore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.claimscore.domain.provider.Provider;

/**
 * Spring Data MongoDB repository for {@link Provider} documents.
 *
 * <p>Provides CRUD operations and domain-specific query methods for the {@code providers}
 * collection.
 */
public interface ProviderRepository extends MongoRepository<Provider, String> {

    /**
     * Finds a provider by their National Provider Identifier (NPI).
     *
     * @param npi the 10-digit NPI to search for
     * @return an {@link Optional} containing the matching provider, or empty if not found
     */
    Optional<Provider> findByNpi(String npi);
}
