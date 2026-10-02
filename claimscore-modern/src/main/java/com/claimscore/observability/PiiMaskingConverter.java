package com.claimscore.observability;

import java.util.regex.Pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Logback {@link ClassicConverter} that masks Social Security Number (SSN) patterns in log
 * messages to prevent accidental PII exposure (PI-009).
 *
 * <p>Any substring matching the pattern {@code \d{3}-\d{2}-\d{4}} is replaced with
 * {@code [MASKED]} before the message is written to any appender.
 *
 * <p>Register this converter in {@code logback-spring.xml} as a conversion rule, then reference
 * it in the encoder pattern as {@code %maskedMsg}.
 */
public class PiiMaskingConverter extends ClassicConverter {

  /** Regex that matches a US Social Security Number in {@code NNN-NN-NNNN} format. */
  private static final Pattern SSN_PATTERN = Pattern.compile("\\d{3}-\\d{2}-\\d{4}");

  /** Replacement token used in place of a detected SSN. */
  private static final String MASK = "[MASKED]";

  /**
   * Returns the log message with all SSN patterns replaced by {@code [MASKED]}.
   *
   * @param event the logging event whose formatted message will be masked
   * @return the masked message string
   */
  @Override
  public String convert(ILoggingEvent event) {
    return mask(event.getFormattedMessage());
  }

  /**
   * Masks all SSN patterns in the supplied string.
   *
   * <p>This method is package-accessible for direct unit testing without a full Logback context.
   *
   * @param message the raw log message
   * @return the message with SSN patterns replaced by {@code [MASKED]}
   */
  String mask(String message) {
    if (message == null) {
      return null;
    }
    return SSN_PATTERN.matcher(message).replaceAll(MASK);
  }
}
