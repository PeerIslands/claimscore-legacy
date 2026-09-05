# ClaimsCore database

Oracle 11g schema for ClaimsCore 2.7, owner schema `CLAIMSCORE`. Everything here is synthetic:
every code, name, hash and identifier is invented.

| File | Contents |
|---|---|
| `ddl/oracle-schema.sql` | 47 tables, 13 sequences, indexes, named constraints, a `COMMENT ON TABLE` per table |
| `seed/reference-data.sql` | reference rows: every `LK_*` table, `LK_DENIAL_REASON`, `APP_ROLES`, `ICD_CODES`, `CPT_CODES`, `CPT_FEE_SCHEDULE` (2025), 4 `PLANS` with limits and copays, 3 `WORK_QUEUES`, 3 `APP_USERS`; sample master data: 3 `PROVIDERS` with specialties and contracts, 2 `POLICY_HOLDERS` with addresses, contacts, a dependent and enrollments |
| `packages/PKG_CLAIMS.pks` / `.pkb` | the adjudication package |
| `triggers/TRG_CLAIM_AUDIT.trg` | audit trigger on `CLAIMS` |

Load order: `ddl/oracle-schema.sql`, `seed/reference-data.sql`, `packages/PKG_CLAIMS.pks`,
`packages/PKG_CLAIMS.pkb`, `triggers/TRG_CLAIM_AUDIT.trg`.

## The 47 -> 12 grouping

The 47 Oracle tables collapse into 12 MongoDB collections plus a set of enums that need no
collection at all. The table below is the **design target for the data-migration stage, not a
measurement** of anything the current system does; the legacy code has no notion of these groups.

| Oracle tables | Target collection | Strategy | Why |
|---|---|---|---|
| `CLAIMS`, `CLAIM_LINES`, `CLAIM_LINE_DIAGNOSES`, `CLAIM_NOTES`, `CLAIM_ATTACHMENTS`, `CLAIM_STATUS_HISTORY`, `CLAIM_PAYMENTS`, `CLAIM_DENIAL_DETAILS` | `claims` | embed | Every child row is read and written with its claim; lines, notes, history and denials never outlive the header. One document per claim ends the N+1 in `ClaimDAOImpl.findClaimsWithLines`. |
| `CLAIM_AUDIT_LOG` | `claimAuditEvents` | standalone (reference by `claimId`) | Append-only, unbounded, must survive a claim delete (no FK to CLAIMS today; only ACTION_CD -> LK_AUDIT_ACTION). Keeping it out of the claim document caps document growth. |
| `POLICY_HOLDERS`, `PH_ADDRESSES`, `PH_CONTACTS`, `PH_DEPENDENTS`, `PH_PLAN_ENROLLMENTS` | `policyHolders` | embed | Addresses, contacts, dependents and enrollment periods are small, bounded lists owned by the member. `PolicyHolderDAOImpl.findById` already assembles the header, active enrollment and addresses with three statements; contacts and dependents are never read by the Java layer. |
| `PROVIDERS`, `PROVIDER_SPECIALTIES`, `PROVIDER_CONTRACTS` | `providers` | embed | A provider has a handful of specialties and contracts; claims reference the provider by id. |
| `PLANS`, `PLAN_COVERAGE_LIMITS`, `PLAN_COPAY_SCHEDULE` | `plans` | embed | A plan is read whole during adjudication (deductible, coinsurance, per-category limits, per-POS copays). Small and rarely changed. |
| `MEMBER_DEDUCTIBLES`, `MEMBER_OOP_ACCUM` | `memberAccumulators` | embed (one document per member, plan and plan year) | Both tables share the key (`PH_ID`, `PLAN_ID`, `PLAN_YEAR`) and are updated together in `ADJUDICATE_CLAIM`; one document makes the update atomic. |
| `ICD_CODES` | `icdCodes` | standalone reference | Large, versioned code set maintained by an external feed. |
| `CPT_CODES`, `CPT_FEE_SCHEDULE` | `cptCodes` | standalone reference, fee schedule embedded per plan year | The fee lookup is always by (code, plan year); a `fees` array keyed by year on the code document serves it in one read. |
| `LK_DENIAL_REASON` | `denialReasons` | standalone | Unlike the other `LK_*` tables this one carries member-facing long text and is edited by operations, so it is data, not an enum. |
| `APP_USERS`, `APP_ROLES`, `APP_USER_ROLES` | `users` | embed roles | Roles are a short string array on the user; `APP_ROLES` becomes an enum in the application. |
| `REMIT_BATCHES`, `REMIT_ITEMS` | `remittances` | embed | A batch and its items are produced and read together; claims reference the remittance item (`CLAIM_PAYMENTS.REMIT_ITEM_ID`), which resolves to its batch; the target payment sub-document should carry both `remitItemId` and the denormalised `batchId`. |
| `WORK_QUEUES`, `WORK_ITEMS` | `workItems` | embed queue | The queue name and priority are denormalised onto the item; `WORK_QUEUES` is three rows and becomes configuration. |
| `LK_CLAIM_STATUS`, `LK_CLAIM_TYPE`, `LK_LINE_STATUS`, `LK_PLACE_OF_SERVICE`, `LK_PROVIDER_TYPE`, `LK_SPECIALTY`, `LK_STATE`, `LK_ADDRESS_TYPE`, `LK_CONTACT_TYPE`, `LK_RELATIONSHIP`, `LK_PLAN_TYPE`, `LK_NOTE_TYPE`, `LK_PAYMENT_METHOD`, `LK_AUDIT_ACTION` | (none) | enum | Fixed code lists with a name and sort order; they become Java enums and are stored as their code string. `LookupDAO` caches them forever today, which is the same thing done badly. |

Group counts: 8 + 1 + 5 + 3 + 3 + 2 + 1 + 2 + 1 + 3 + 2 + 2 + 14 = 47.

## Sequences

Thirteen sequences, all `INCREMENT BY 1 NOCACHE NOCYCLE`: `SEQ_CLAIM` (starts at 500000),
`SEQ_CLAIM_LINE`, `SEQ_CLAIM_NOTE`, `SEQ_CLAIM_HIST`, `SEQ_CLAIM_PAYMENT`, `SEQ_CLAIM_DENIAL`,
`SEQ_AUDIT`, `SEQ_POLICY_HOLDER`, `SEQ_PH_ADDRESS`, `SEQ_PROVIDER`, `SEQ_WORK_ITEM`, `SEQ_REMIT`,
`SEQ_ACCUM`. The Java DAOs fetch ids with `SELECT SEQ_X.NEXTVAL FROM DUAL` before each insert;
PL/SQL uses `SEQ_X.NEXTVAL` inline. Reference tables seeded by hand (`PLANS`,
`CPT_FEE_SCHEDULE`, `PLAN_COVERAGE_LIMITS`, `PLAN_COPAY_SCHEDULE`, `WORK_QUEUES`, `APP_USERS`)
have no sequence and use literal ids in `seed/reference-data.sql`. The sample providers, members and
addresses are inserted with `SEQ_PROVIDER.NEXTVAL`, `SEQ_POLICY_HOLDER.NEXTVAL` and
`SEQ_PH_ADDRESS.NEXTVAL` so application inserts never collide with seeded ids; their child rows look
the parent up by `NPI` or `MEMBER_NUM`.

## What `PKG_CLAIMS` encodes

`PKG_CLAIMS.ADJUDICATE_CLAIM` is where the business rules live, not the Java. In order it locks
the claim (`FOR UPDATE NOWAIT`) and checks the status is `REC`, `PND` or `REO`; denies the whole
claim `TIMELY` when it was received more than 365 days after the incident; denies `DUP30` when
another non-denied, non-voided claim for the same member and provider within a 30-day window
shares a service code; denies `NOENR` when no enrollment covers the incident date; then prices
each line from `CPT_FEE_SCHEDULE` (falling back silently to 80% of billed when there is no fee
row), caps it at the plan's per-claim coverage limit and denies the line `LIMIT` when the annual
category limit would be exceeded; applies the member's remaining deductible from
`MEMBER_DEDUCTIBLES`; adds the place-of-service copay and coinsurance through
`CALC_PATIENT_LIABILITY`, capped by the out-of-pocket maximum in `MEMBER_OOP_ACCUM`; distributes
the paid amount across approved lines pro rata; and sets the outcome to `APR`, `PAR` or `DEN`.
`INVSVC` cannot fire in practice: `FK_CLAIM_LINES_CPT_CODES` already rejects unknown service codes at
insert time, so the branch is dead code.
A paid amount of 25000 or more is not auto-approved: the claim is parked in `PND` and a
`HIGH_DOLLAR` work item is queued. `REOPEN_CLAIM` enforces the 90-day reopen window and queues an
`APPEALS` item but never reverses the accumulators (INC-4471). Every status change is written to
`CLAIM_STATUS_HISTORY` by the package and, separately, to `CLAIM_AUDIT_LOG` by `TRG_CLAIM_AUDIT`
on every insert or update of `CLAIMS`; `AuditLogDAO` in the Java layer writes to the same audit
table a second time. The package does not commit; the calling EJB's container transaction does.
