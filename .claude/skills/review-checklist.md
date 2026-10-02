# Skill: Code Review Checklist

Use this checklist when reviewing any PR that touches `claimscore-modern/`.

## Correctness
- [ ] No `double` or `float` for monetary amounts — must be `BigDecimal`
- [ ] No `java.util.Date` or `java.sql.Date` — must be `java.time.*`
- [ ] No String constants for status codes — must be Java enums
- [ ] No hard-coded credentials, URLs, or secrets — must use environment variables / `@Value`
- [ ] No `@Autowired` field injection — must use constructor injection
- [ ] Null safety: `Optional` used where absence is expected; no unchecked `.get()`

## Security
- [ ] All endpoints are either explicitly secured or explicitly `permitAll()` in `SecurityConfig`
- [ ] No sensitive data logged (PII, claim amounts, member IDs in plain log lines)
- [ ] Input validation annotations present on all controller request bodies (`@Valid`)

## MongoDB / persistence
- [ ] `@Document` collection name is explicit (not defaulted)
- [ ] Indexes declared via `@Indexed` or `@CompoundIndex` where queries filter/sort
- [ ] No `MongoTemplate` raw queries without a comment explaining why the repository abstraction is insufficient

## Testing
- [ ] New service logic has unit tests (Mockito)
- [ ] New repository queries have integration tests (Testcontainers MongoDB)
- [ ] Test class name ends in `Test` (required by Surefire pattern `**/*Test.java`)
- [ ] No `Thread.sleep()` in tests — use Awaitility or test doubles

## Build hygiene
- [ ] `mvn spotless:check` passes (run `spotless:apply` to fix)
- [ ] `mvn checkstyle:check` passes
- [ ] `mvn dependency:analyze -DfailOnWarning=true` passes (no unused declared / used undeclared deps)
- [ ] `mvn compile -Werror` passes (no compiler warnings)

## Legacy reference
- [ ] Legacy code in `src/` (root) was NOT modified
- [ ] If a legacy business rule was ported, the source class/method is cited in a comment
