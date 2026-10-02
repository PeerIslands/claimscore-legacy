package com.claimscore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test — verifies the Spring application context loads without external infrastructure.
 *
 * <p>MongoDB and OAuth2 auto-configurations are excluded so this test runs without a live MongoDB
 * instance or JWT issuer. Integration tests that exercise persistence use Testcontainers.
 */
@SpringBootTest
@EnableAutoConfiguration(
    exclude = {
      MongoAutoConfiguration.class,
      DataMongoAutoConfiguration.class,
      DataMongoRepositoriesAutoConfiguration.class,
      OAuth2ResourceServerAutoConfiguration.class
    })
class ClaimsScoreApplicationTests {

  @Test
  void contextLoads() {}
}
