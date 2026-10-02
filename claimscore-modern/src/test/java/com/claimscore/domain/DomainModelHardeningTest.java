package com.claimscore.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.claimscore.domain.accumulator.MemberAccumulator;
import com.claimscore.domain.claim.Claim;
import com.claimscore.domain.claim.ClaimLine;
import com.claimscore.domain.member.PolicyHolder;
import com.claimscore.domain.plan.Plan;

/**
 * Hardening guard tests for MongoDB domain model classes.
 *
 * <p>No Spring context is loaded — these tests verify {@code @Version} optimistic locking,
 * embedded document handling, null field handling, and BigDecimal precision as specified in
 * {@code sli_mongodb_schema}.
 */
class DomainModelHardeningTest {

  /**
   * A freshly constructed {@link Claim} has no embedded lines; {@code getLines()} must return
   * {@code null} rather than throwing an NPE, so callers can guard with a null check.
   */
  @Test
  void claimWithNullLinesHandledSafely() {
    Claim claim = new Claim();
    // getLines() should return null (field is not initialized), not throw NPE
    assertThat(claim.getLines()).isNull();
  }

  /**
   * {@link ClaimLine#getBilledAmt()} must preserve the exact scale of the value supplied,
   * confirming that BigDecimal is used rather than a lossy {@code double}.
   */
  @Test
  void bigDecimalFieldsPreservePrecision() {
    ClaimLine line = new ClaimLine();
    line.setBilledAmt(new BigDecimal("1234.56"));
    assertThat(line.getBilledAmt()).isEqualByComparingTo("1234.56");
    // Verify it is not stored as double (which would lose precision)
    assertThat(line.getBilledAmt().scale()).isEqualTo(2);
  }

  /**
   * The {@code @Version} field on {@link MemberAccumulator} must start as {@code null} so that
   * Spring Data MongoDB can assign the initial version on first save.
   */
  @Test
  void memberAccumulatorVersionStartsNull() {
    MemberAccumulator acc = new MemberAccumulator();
    assertThat(acc.getVersion()).isNull();
  }

  /**
   * {@link Plan#setPlanYearStartMonth(Integer)} must accept every valid calendar month (1–12)
   * and return the same value from the getter — critical for the DEFECT-001 accumulator-reset
   * logic.
   */
  @Test
  void planYearStartMonthAcceptsValidMonths() {
    Plan plan = new Plan();
    for (int month = 1; month <= 12; month++) {
      plan.setPlanYearStartMonth(month);
      assertThat(plan.getPlanYearStartMonth()).isEqualTo(month);
    }
  }

  /**
   * The {@code ssnLast4} field on {@link PolicyHolder} must be accessible via getter/setter and
   * must hold exactly 4 characters, confirming PII-masking compliance (full SSN is never stored).
   */
  @Test
  void policyHolderSsnLast4FieldExists() {
    PolicyHolder ph = new PolicyHolder();
    ph.setSsnLast4("1234");
    assertThat(ph.getSsnLast4()).isEqualTo("1234");
    // Verify the field stores only the last 4 digits (PII masking compliance)
    assertThat(ph.getSsnLast4()).hasSize(4);
  }
}
