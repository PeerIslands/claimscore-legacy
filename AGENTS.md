# ClaimsCore Modernization — Agent Rules

This repository contains:
- Legacy ClaimsCore application (Java 8/Struts/EJB/Oracle) — READ ONLY, do not modify
- Modern ClaimsCore application (Java 25/Spring Boot 4/MongoDB) under `claimscore-modern/`

## Working directory
All new code goes under `claimscore-modern/`. Never modify files outside this directory.

## Build commands (run from `claimscore-modern/`)
- Unit tests: `mvn test -Dtest="**/*Test.java" -DfailIfNoTests=false`
- Build: `mvn package -DskipTests`
- Type check: `mvn compile -Werror`
- Static analysis: `mvn checkstyle:check`
- Formatting check: `mvn spotless:check`
- Dependency integrity: `mvn dependency:analyze -DfailOnWarning=true`

## Code conventions
- Package root: `com.claimscore`
- Java 25, Spring Boot 4, Spring Data MongoDB
- All monetary amounts: `BigDecimal` (never `double` or `float`)
- All dates: `java.time.LocalDate` / `java.time.Instant` (never `java.util.Date`)
- Status codes: Java enums (never String constants)
- No hard-coded credentials — use environment variables
