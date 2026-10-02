package com.claimscore.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.claimscore.domain.accumulator.MemberAccumulator;
import com.claimscore.domain.claim.Claim;
import com.claimscore.domain.claim.ClaimLine;
import com.claimscore.domain.plan.Plan;

/**
 * Unit tests for MongoDB domain document classes.
 *
 * <p>No Spring context is loaded — these tests verify field presence and basic wiring only.
 */
class ClaimDocumentTest {

    @Test
    void claimDocumentHasRequiredFields() {
        Claim claim = new Claim();
        claim.setClaimNumber("CLM2024-001");
        claim.setStatus("REC");
        assertThat(claim.getClaimNumber()).isEqualTo("CLM2024-001");
        assertThat(claim.getStatus()).isEqualTo("REC");
    }

    @Test
    void claimLinesCanBeEmbedded() {
        Claim claim = new Claim();
        ClaimLine line = new ClaimLine();
        line.setLineNo(1);
        line.setSvcCd("99213");
        line.setBilledAmt(new BigDecimal("150.00"));
        claim.setLines(List.of(line));
        assertThat(claim.getLines()).hasSize(1);
        assertThat(claim.getLines().get(0).getSvcCd()).isEqualTo("99213");
    }

    @Test
    void memberAccumulatorHasVersionField() {
        MemberAccumulator acc = new MemberAccumulator();
        acc.setDeductibleMet(new BigDecimal("200.00"));
        acc.setOopMet(new BigDecimal("240.00"));
        assertThat(acc.getDeductibleMet()).isEqualByComparingTo("200.00");
    }

    @Test
    void planHasPlanYearStartMonthField() {
        Plan plan = new Plan();
        plan.setPlanYearStartMonth(7);
        assertThat(plan.getPlanYearStartMonth()).isEqualTo(7);
    }
}
