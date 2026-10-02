package com.claimscore.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * Hardening guard tests for reference-data enums.
 *
 * <p>No Spring context is loaded — these tests verify enum completeness, {@code fromCode()}
 * null/unknown-code safety, display-name and member-facing-text completeness, and exact
 * cardinality of {@link LineStatus}, as specified in {@code sli_reference_data}.
 */
class ReferenceDataHardeningTest {

    /**
     * Every {@link ClaimStatus} constant must carry a non-blank display name so that UI
     * rendering never shows an empty label.
     */
    @Test
    void allClaimStatusValuesHaveNonBlankDisplayNames() {
        for (ClaimStatus status : ClaimStatus.values()) {
            assertThat(status.getDisplayName())
                    .as("ClaimStatus.%s has blank display name", status)
                    .isNotBlank();
        }
    }

    /**
     * {@link ClaimStatus#fromCode(String)} is case-sensitive; a lowercase code such as
     * {@code "rec"} must be rejected with an {@link IllegalArgumentException}.
     */
    @Test
    void claimStatusFromCodeIsCaseSensitive() {
        assertThatThrownBy(() -> ClaimStatus.fromCode("rec"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Every {@link DenialReasonCode} constant must carry non-blank member-facing text so that
     * EOB documents never display an empty denial explanation.
     */
    @Test
    void allDenialReasonCodesHaveNonBlankMemberFacingText() {
        for (DenialReasonCode code : DenialReasonCode.values()) {
            assertThat(code.getMemberFacingText())
                    .as("DenialReasonCode.%s has blank member-facing text", code)
                    .isNotBlank();
        }
    }

    /**
     * {@link PlaceOfService#fromCode(String)} must throw an {@link IllegalArgumentException}
     * whose message contains the unknown code when given a code that does not exist (e.g.
     * {@code "99"}).
     */
    @Test
    void placeOfServiceFromCodeThrowsForUnknownCode() {
        assertThatThrownBy(() -> PlaceOfService.fromCode("99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    /**
     * {@link LineStatus} must declare exactly three constants — {@code APR}, {@code DEN}, and
     * {@code PND} — matching the Oracle {@code LK_LINE_STATUS} lookup table.
     */
    @Test
    void lineStatusHasExactlyThreeValues() {
        assertThat(LineStatus.values()).hasSize(3);
        assertThat(LineStatus.values())
                .containsExactlyInAnyOrder(LineStatus.APR, LineStatus.DEN, LineStatus.PND);
    }
}
