# Skill: Run Project Gates

Run all 6 wave-0 gates for the ClaimsCore modern project and report results.

## When to use
- Before committing a feature branch
- After any structural change to `claimscore-modern/`
- When asked to "verify gates" or "run all checks"

## Steps

```bash
cd claimscore-modern

# 1. typeSafety
mvn compile -Werror -B

# 2. unitTests
mvn test -Dtest="**/*Test.java" -DfailIfNoTests=false -B

# 3. buildPackage
mvn package -DskipTests -B

# 4. staticAnalysis
mvn checkstyle:check -B

# 5. formatting
mvn spotless:check -B

# 6. dependencyIntegrity
mvn dependency:analyze -DfailOnWarning=true -B
```

## Reporting results

For each gate report: command, exit code (0 = pass, non-zero = fail), and any error summary.

| Gate | Command | Status |
|------|---------|--------|
| typeSafety | `mvn compile -Werror` | ✅/❌ |
| unitTests | `mvn test -Dtest="**/*Test.java" -DfailIfNoTests=false` | ✅/❌ |
| buildPackage | `mvn package -DskipTests` | ✅/❌ |
| staticAnalysis | `mvn checkstyle:check` | ✅/❌ |
| formatting | `mvn spotless:check` | ✅/❌ |
| dependencyIntegrity | `mvn dependency:analyze -DfailOnWarning=true` | ✅/❌ |

## Fixing failures

- **formatting**: run `mvn spotless:apply` then re-check
- **staticAnalysis**: fix violations reported in `target/checkstyle-result.xml`
- **typeSafety**: fix compiler errors; `-Werror` promotes warnings to errors
- **dependencyIntegrity**: add missing `<scope>` or remove unused deps from pom.xml
