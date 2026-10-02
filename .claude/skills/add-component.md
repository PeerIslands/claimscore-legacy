# Skill: Add a New Spring Boot Component

Add a new domain component (entity, repository, service, controller) to `claimscore-modern/`.

## Package layout

```
com.claimscore
├── domain/          ← MongoDB documents (@Document)
├── repository/      ← Spring Data repositories (extends MongoRepository)
├── service/         ← Business logic (@Service)
├── web/
│   └── rest/        ← REST controllers (@RestController)
├── dto/             ← Request/response DTOs (records preferred)
├── config/          ← @Configuration classes
└── exception/       ← Custom exceptions and @ControllerAdvice
```

## Steps for a new domain entity (example: `Claim`)

### 1. Domain document
```java
// src/main/java/com/claimscore/domain/Claim.java
package com.claimscore.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "claims")
public record Claim(
    @Id String id,
    String claimNumber,
    BigDecimal amount,       // ALWAYS BigDecimal for money
    LocalDate serviceDate,   // ALWAYS java.time for dates
    ClaimStatus status       // ALWAYS enum for status codes
) {}
```

### 2. Repository
```java
// src/main/java/com/claimscore/repository/ClaimRepository.java
package com.claimscore.repository;

import com.claimscore.domain.Claim;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClaimRepository extends MongoRepository<Claim, String> {
    // add query methods as needed
}
```

### 3. Service
```java
// src/main/java/com/claimscore/service/ClaimService.java
package com.claimscore.service;

import com.claimscore.repository.ClaimRepository;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {
    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }
    // business methods
}
```

### 4. REST controller
```java
// src/main/java/com/claimscore/web/rest/ClaimController.java
package com.claimscore.web.rest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/claims")
public class ClaimController {
    // inject service via constructor
}
```

### 5. Test
```java
// src/test/java/com/claimscore/service/ClaimServiceTest.java
@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {
    @Mock ClaimRepository claimRepository;
    @InjectMocks ClaimService claimService;

    @Test
    void shouldDoSomething() { ... }
}
```

## Conventions checklist
- [ ] Money → `BigDecimal`
- [ ] Dates → `LocalDate` / `Instant`
- [ ] Status → enum
- [ ] No hard-coded credentials
- [ ] Constructor injection (not field injection)
- [ ] Run `mvn spotless:apply` before committing
