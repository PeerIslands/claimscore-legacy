package com.claimscore.mongo;

/**
 * Names of the MongoDB collections targeted by the data-access move.
 *
 * <p>Only the tables that carry data become collections. The LK_* lookup
 * tables (LK_CLAIM_STATUS, LK_CLAIM_TYPE, LK_LINE_STATUS, LK_PLACE_OF_SERVICE,
 * LK_PROVIDER_TYPE, LK_SPECIALTY, LK_STATE, LK_ADDRESS_TYPE, LK_CONTACT_TYPE,
 * LK_RELATIONSHIP, LK_PLAN_TYPE, LK_NOTE_TYPE, LK_PAYMENT_METHOD and
 * LK_AUDIT_ACTION) stay enums in the application and have no collection.</p>
 *
 * <p>This class holds names only: no client, no connection string and no way
 * to open a database.</p>
 */
public final class MongoCollections {

    public static final String CLAIMS = "claims";
    public static final String CLAIM_AUDIT_EVENTS = "claimAuditEvents";
    public static final String POLICY_HOLDERS = "policyHolders";
    public static final String PROVIDERS = "providers";
    public static final String PLANS = "plans";
    public static final String MEMBER_ACCUMULATORS = "memberAccumulators";
    public static final String ICD_CODES = "icdCodes";
    public static final String CPT_CODES = "cptCodes";
    public static final String DENIAL_REASONS = "denialReasons";
    public static final String USERS = "users";
    public static final String REMITTANCES = "remittances";
    public static final String WORK_ITEMS = "workItems";

    private MongoCollections() {
    }
}
