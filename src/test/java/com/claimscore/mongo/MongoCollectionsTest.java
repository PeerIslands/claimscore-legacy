package com.claimscore.mongo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Unit tests for the MongoDB collection names targeted by the data-access move.
 */
public class MongoCollectionsTest {

    @Test
    public void testCollectionNames() {
        assertEquals("claims", MongoCollections.CLAIMS);
        assertEquals("claimAuditEvents", MongoCollections.CLAIM_AUDIT_EVENTS);
        assertEquals("policyHolders", MongoCollections.POLICY_HOLDERS);
        assertEquals("providers", MongoCollections.PROVIDERS);
        assertEquals("plans", MongoCollections.PLANS);
        assertEquals("memberAccumulators", MongoCollections.MEMBER_ACCUMULATORS);
        assertEquals("icdCodes", MongoCollections.ICD_CODES);
        assertEquals("cptCodes", MongoCollections.CPT_CODES);
        assertEquals("denialReasons", MongoCollections.DENIAL_REASONS);
        assertEquals("users", MongoCollections.USERS);
        assertEquals("remittances", MongoCollections.REMITTANCES);
        assertEquals("workItems", MongoCollections.WORK_ITEMS);
    }

    @Test
    public void testCollectionNamesAreDistinct() {
        List<String> names = Arrays.asList(
                MongoCollections.CLAIMS,
                MongoCollections.CLAIM_AUDIT_EVENTS,
                MongoCollections.POLICY_HOLDERS,
                MongoCollections.PROVIDERS,
                MongoCollections.PLANS,
                MongoCollections.MEMBER_ACCUMULATORS,
                MongoCollections.ICD_CODES,
                MongoCollections.CPT_CODES,
                MongoCollections.DENIAL_REASONS,
                MongoCollections.USERS,
                MongoCollections.REMITTANCES,
                MongoCollections.WORK_ITEMS);

        Set<String> distinct = new HashSet<String>(names);
        assertEquals(12, names.size());
        assertEquals(12, distinct.size());
    }
}
