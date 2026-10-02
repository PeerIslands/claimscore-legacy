# ClaimsCore Modernization

This repository contains the ClaimsCore health-plan claims processing system in two states:

| Directory | Stack | Status |
|-----------|-------|--------|
| `src/` (root) | Java 8 / Struts 1 / EJB 3 / Oracle | Legacy — read-only reference |
| `claimscore-modern/` | Java 25 / Spring Boot 4 / MongoDB | Active modernization target |

## Background

The legacy ClaimsCore application (Larkspur Mutual Health Plan) was built on Java EE / WebLogic circa 2010–2016. This modernization effort migrates it to a cloud-native Spring Boot 4 service backed by MongoDB, with full observability, OAuth2/JWT security, and a clean domain model.

## Quick start (modern application)

```bash
cd claimscore-modern

# Run locally (requires MongoDB on localhost:27017)
mvn spring-boot:run

# Run tests
mvn test -Dtest="**/*Test.java" -DfailIfNoTests=false

# Build fat JAR
mvn package -DskipTests
```

## Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `MONGODB_URI` | `mongodb://localhost:27017/claimscore` | MongoDB connection string |
| `JWT_ISSUER_URI` | `http://localhost:8080/auth/realms/claimscore` | OAuth2 JWT issuer |

## Architecture

See `claimscore-modern/` for the full Spring Boot 4 project. The domain model, REST API, and persistence layer are being built incrementally from the legacy source as the reference.

## Contributing

All new code goes under `claimscore-modern/`. See `AGENTS.md` for coding conventions and gate commands.
