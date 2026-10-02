package com.claimscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** ClaimsCore Modern — Spring Boot 4 entry point. */
@SpringBootApplication
public class ClaimsScoreApplication {

  /** Private constructor — utility class. */
  private ClaimsScoreApplication() {}

  public static void main(String[] args) {
    SpringApplication.run(ClaimsScoreApplication.class, args);
  }
}
