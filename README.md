# ClaimsCore 2.7 (legacy fixture)

ClaimsCore is a **synthetic** claims-adjudication system for a fictional regional health insurer,
Larkspur Mutual Health Plan. It is written the way a Java EE application of 2008 was written:
Java 8 (compiler moved up in 2016), Struts 1.3, EJB 2.x and EJB 3.0 side by side, raw JDBC and an
Oracle 11g PL/SQL package that holds most of the business rules. Every company, person, provider,
code and identifier is invented. It is read as source text by modernization tooling; it is not
deployed anywhere. It does compile: `mvn -q -DskipTests package` on JDK 8.

## Purpose

This repository is the as-is fixture for the Protean AIDLC modernization demo. The demo takes a
Java 8 / Struts 1 / EJB 2+3 / JDBC + Oracle 11g PL/SQL application and produces a Java 25 /
Spring Boot 4.1 / Spring Data MongoDB 5.1 target. The code here is deliberately unmodernized so
that the analysis stages (`code-insights`, `specs`, `data-migration`) have something real to find.
The smells listed below are intentional; do not fix them in this repository.

## Layout

```
claimscore-legacy/
  README.md
  LICENSE                         # MIT, Copyright (c) 2026 PeerIslands
  .gitignore
  pom.xml
  .github/workflows/build.yml
  .github/scripts/shape-check.sh
  db/README.md                    # the 47 -> 12 grouping
  db/ddl/oracle-schema.sql        # 47 CREATE TABLE + 13 sequences + indexes + FKs
  db/seed/reference-data.sql      # lookup rows, roles, codes, plans, work queues, users, sample providers and members; synthetic
  db/packages/PKG_CLAIMS.pks
  db/packages/PKG_CLAIMS.pkb
  db/triggers/TRG_CLAIM_AUDIT.trg
  src/main/java/com/claimscore/struts/{LoginAction,ClaimAction,ClaimSearchAction,PolicyHolderAction,ClaimForm}.java
  src/main/java/com/claimscore/ejb/{ClaimService,ClaimServiceBean,ClaimServiceException,AdjudicationService,AdjudicationServiceHome,AdjudicationServiceBean,PolicyHolderService,PolicyHolderServiceBean}.java
  src/main/java/com/claimscore/dao/{ClaimDAO,ClaimDAOImpl,ClaimLineDAO,PolicyHolderDAO,PolicyHolderDAOImpl,ProviderDAO,LookupDAO,AuditLogDAO}.java
  src/main/java/com/claimscore/dto/{ClaimDTO,ClaimLineDTO,PolicyHolderDTO,AddressDTO,ProviderDTO,AdjudicationResultDTO}.java
  src/main/java/com/claimscore/util/{OracleConnectionFactory,DateUtil,MoneyUtil,ClaimStatus,Constants}.java
  src/main/resources/com/claimscore/ApplicationResources.properties
  src/main/resources/log4j.properties
  src/main/resources/META-INF/ejb-jar.xml
  src/main/resources/META-INF/weblogic-ejb-jar.xml
  src/main/webapp/WEB-INF/web.xml
  src/main/webapp/WEB-INF/struts-config.xml
  src/main/webapp/WEB-INF/weblogic.xml
  src/main/webapp/WEB-INF/jsp/{login,error}.jsp
  src/main/webapp/WEB-INF/jsp/claim/{view,search,submit}.jsp
  src/main/webapp/WEB-INF/jsp/policyholder/view.jsp
  src/main/webapp/index.jsp       # one-line redirect to login.do
  src/test/java/com/claimscore/util/{DateUtilTest,MoneyUtilTest,ClaimStatusTest}.java   # JUnit 4
```

## The application

Five layers, top to bottom:

1. **Presentation** — Struts 1.3 Actions and JSPs (`com.claimscore.struts`, `WEB-INF/jsp`).
   `ClaimForm` is the one `ActionForm`; login uses a `DynaActionForm`.
2. **Service** — session beans in `com.claimscore.ejb`. `ClaimServiceBean` and
   `PolicyHolderServiceBean` are EJB 3.0 `@Stateless` beans with `@Local` interfaces.
   `AdjudicationServiceBean` is an EJB 2.x stateless session bean with a home and a remote
   interface, declared in `ejb-jar.xml` and `weblogic-ejb-jar.xml`.
3. **Data access** — hand-written JDBC DAOs in `com.claimscore.dao`, all going through
   `OracleConnectionFactory` (JNDI `DataSource` with a hard-coded `DriverManager` fallback).
4. **Database logic** — `PKG_CLAIMS` (adjudication, reopen, void, patient-liability calculation)
   and the `TRG_CLAIM_AUDIT` trigger. Adjudication is a stored procedure; Java only calls it.
5. **Storage** — Oracle 11g, 47 tables, 13 sequences.

The request path is `Struts Action -> EJB -> DAO / PL-SQL -> Oracle`. Claim submission goes
Action -> `ClaimService` (EJB 3) -> `ClaimDAOImpl`; adjudication goes Action -> JNDI home lookup ->
`AdjudicationService` (EJB 2.x) -> `PKG_CLAIMS.ADJUDICATE_CLAIM`. Both generations of EJB live in
the same module and are wired differently on purpose.

Transfer objects (`com.claimscore.dto`) are `Serializable` beans with `java.util.Date` and
`BigDecimal` fields. Utilities (`com.claimscore.util`) hold the status codes, the money and date
helpers, and the constant interface.

## The database

`db/ddl/oracle-schema.sql` creates 47 tables and 13 sequences with primary keys, foreign keys,
unique constraints and indexes. `db/seed/reference-data.sql` loads the lookup tables, roles,
diagnosis and service codes, four plans with their limits and copays, three work queues, three
application users, and a small set of sample master data (three providers, two members with
addresses, contacts, a dependent and plan enrollments). No claims are seeded; the application
creates those.
`db/packages/PKG_CLAIMS.pks` / `.pkb` hold the adjudication rules (timely filing, 30-day
duplicate window, enrollment check, fee schedule with an 80% fallback, per-claim and annual
coverage limits, deductible and out-of-pocket accumulators, copay and coinsurance, high-dollar
routing to a work queue). `db/triggers/TRG_CLAIM_AUDIT.trg` writes `CLAIM_AUDIT_LOG` on every
insert or update of `CLAIMS`.

The 47 tables fall into 13 groups that map onto 12 target MongoDB collections plus a set of enums.
That grouping, with the strategy (embed, reference, enum, standalone) and the reasoning per group,
is in [`db/README.md`](db/README.md). It is the design target for the data-migration stage, not a
measurement of what the code does today.

## Legacy smells

Each item below exists at the stated place. A reviewer can tick them off against the source.

- [x] 1. N+1 query — `ClaimDAOImpl.findClaimsWithLines`: one `PreparedStatement` per claim inside `for (ClaimDTO claim : claims)`. (`src/main/java/com/claimscore/dao/ClaimDAOImpl.java`)
- [x] 2. Business rules in the database — `PKG_CLAIMS.ADJUDICATE_CLAIM` (deductible, coverage limit, 30-day duplicate window, status transitions) and the audit trigger. (`db/packages/PKG_CLAIMS.pkb`, `db/triggers/TRG_CLAIM_AUDIT.trg`)
- [x] 3. Two audit writers — `TRG_CLAIM_AUDIT` and `AuditLogDAO.log` both insert into `CLAIM_AUDIT_LOG`. (`db/triggers/TRG_CLAIM_AUDIT.trg`, `src/main/java/com/claimscore/dao/AuditLogDAO.java`)
- [x] 4. `java.util.Date` in every DTO that carries a date (`ClaimDTO`, `ClaimLineDTO`, `PolicyHolderDTO`, `AddressDTO`, `AdjudicationResultDTO`), `Calendar` arithmetic in `DateUtil`, a shared static `SimpleDateFormat`. (`src/main/java/com/claimscore/dto/*.java`, `src/main/java/com/claimscore/util/DateUtil.java`)
- [x] 5. Money as `double` in `ClaimSearchAction` totals; `BigDecimal.ROUND_HALF_UP` int constants in `MoneyUtil`. (`src/main/java/com/claimscore/struts/ClaimSearchAction.java`, `src/main/java/com/claimscore/util/MoneyUtil.java`)
- [x] 6. String status codes, no enum — `ClaimStatus`; constant interface — `Constants`. (`src/main/java/com/claimscore/util/ClaimStatus.java`, `src/main/java/com/claimscore/util/Constants.java`)
- [x] 7. Hard-coded fallback JDBC URL and password — `Constants` / `OracleConnectionFactory`. (`src/main/java/com/claimscore/util/Constants.java`, `src/main/java/com/claimscore/util/OracleConnectionFactory.java`) The value `cl41msc0re` is a deliberately fake placeholder (the product name in leetspeak), as is the host `clmdb01.larkspur.local`; nothing in this repository has ever been a live credential. The three `APP_USERS` rows in `db/seed/reference-data.sql` are the MD5 of `password`, `123456` and `admin`.
- [x] 8. Swallowed exceptions — `OracleConnectionFactory.close`; log-and-rethrow that loses context in `PKG_CLAIMS`. (`src/main/java/com/claimscore/util/OracleConnectionFactory.java`, `db/packages/PKG_CLAIMS.pkb`)
- [x] 9. SQL built by string concatenation — `LookupDAO.loadLookup`; never-invalidated static `Hashtable` cache. (`src/main/java/com/claimscore/dao/LookupDAO.java`)
- [x] 10. SQL in the presentation layer — `LoginAction`; DAO called from an Action — `ClaimAction` (`AuditLogDAO`); SQL in an EJB — `ClaimServiceBean.addNote`. (`src/main/java/com/claimscore/struts/LoginAction.java`, `src/main/java/com/claimscore/struts/ClaimAction.java`, `src/main/java/com/claimscore/ejb/ClaimServiceBean.java`)
- [x] 11. MD5 password hashing — `LoginAction`; `PASSWORD_HASH VARCHAR2(32)`. (`src/main/java/com/claimscore/struts/LoginAction.java`, `db/ddl/oracle-schema.sql`)
- [x] 12. Two EJB generations — `@Stateless ClaimServiceBean` next to the EJB 2.x `AdjudicationServiceBean` with home/remote interfaces and `ejb-jar.xml`. (`src/main/java/com/claimscore/ejb/ClaimServiceBean.java`, `src/main/java/com/claimscore/ejb/AdjudicationServiceBean.java`, `src/main/resources/META-INF/ejb-jar.xml`)
- [x] 13. JNDI lookups per request, no caching — `ClaimAction`, `PolicyHolderAction`. (`src/main/java/com/claimscore/struts/ClaimAction.java`, `src/main/java/com/claimscore/struts/PolicyHolderAction.java`)
- [x] 14. Ids from Oracle sequences fetched with `SELECT SEQ_X.NEXTVAL FROM DUAL` — `ClaimDAOImpl.insert`, `ClaimLineDAO.insert`. (`src/main/java/com/claimscore/dao/ClaimDAOImpl.java`, `src/main/java/com/claimscore/dao/ClaimLineDAO.java`)
- [x] 15. Manual transaction handling next to CMT — `ClaimDAOImpl.insert` (`setAutoCommit(false)`/`commit`) inside a container-managed EJB. (`src/main/java/com/claimscore/dao/ClaimDAOImpl.java`, `src/main/java/com/claimscore/ejb/ClaimServiceBean.java`)
- [x] 16. Untyped rows — `AuditLogDAO.findByClaimId` returns `List<Map<String, Object>>`. (`src/main/java/com/claimscore/dao/AuditLogDAO.java`)
- [x] 17. Deductible/OOP accumulators never reversed on reopen — `PKG_CLAIMS.REOPEN_CLAIM` TODO (INC-4471). (`db/packages/PKG_CLAIMS.pkb`)
- [x] 18. Magic numbers — 0.80 fallback fee in the package; 25000 high-dollar threshold and 30/90/365-day windows in both `Constants` and the package (duplicated in two languages). (`src/main/java/com/claimscore/util/Constants.java`, `db/packages/PKG_CLAIMS.pks`, `db/packages/PKG_CLAIMS.pkb`)
- [x] 19. Dated dependency set — log4j 1.2.17, Struts 1.3.10, commons-lang 2.6 (the pom is the evidence). (`pom.xml`)
- [x] 20. Raw `List`/unchecked casts — at least one `List` raw type in `ClaimSearchAction` (`List claims = ...` with `@SuppressWarnings("unchecked")` absent, so the build shows an unchecked warning). (`src/main/java/com/claimscore/struts/ClaimSearchAction.java`)

## Building

Requires JDK 8 and Maven 3. Every dependency is on Maven Central; the servlet, JSP, EJB and
Oracle JDBC APIs are `provided` scope and are only needed to compile.

```
mvn -q -DskipTests package     # builds target/claimscore.war
mvn -q test                    # the three JUnit 4 utility tests
bash .github/scripts/shape-check.sh
```

The shape check (`.github/scripts/shape-check.sh`) verifies that the repository still has the
shape the demo depends on: 47 `CREATE TABLE` lines, the PL/SQL package, at least four classes per
layer, the four deployment descriptors, the N+1 loop in `ClaimDAOImpl`, the file-size and
extension limits, and the word "synthetic" at the top of this file. It prints one `ok -` or
`FAIL -` line per check and exits non-zero if any check failed. It runs on both GNU and BSD
userlands.

CI (`.github/workflows/build.yml`) runs on every push and pull request: Temurin JDK 8, package,
test, shape check.

The project also compiles on JDK 21 with `-source 1.8 -target 1.8`; nothing removed from the JDK
after 8 is used.

## Using it with Protean AIDLC

Create an AIDLC project with a `git` connection to this repository, or point a `local-dir`
project at a clone, and run `code-insights` against it. The expected output lists the five
layers under architecture and `PKG_CLAIMS` under data; the smells above should appear in the
modernization assessment. `db/README.md` is the input the data-migration stage is meant to
confirm or correct.

## Provenance

Authored 2026-09-04 with Claude Code for PeerIslands; synthetic data only; no real PHI, NPI, SSN or
CPT content; service codes are invented `SVC-` codes, diagnosis codes are invented `DX-` codes,
provider NPIs start with `99`.

Last smoke: not yet run.

## License

MIT. See [`LICENSE`](LICENSE).
