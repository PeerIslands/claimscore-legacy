package com.claimscore.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for reference-data Java enums.
 *
 * <p>No Spring context is loaded — these tests verify enum values, {@code fromCode()} behaviour,
 * and member-facing text on {@link DenialReasonCode}.
 */
class ReferenceDataEnumTest {

    /** Verifies that {@link ClaimStatus#fromCode(String)} returns the correct constant. */
    @Test
    void claimStatusFromCodeReturnsCorrectEnum() {
        assertThat(ClaimStatus.fromCode("REC")).isEqualTo(ClaimStatus.REC);
        assertThat(ClaimStatus.fromCode("APR")).isEqualTo(ClaimStatus.APR);
    }

    /** Verifies that {@link ClaimStatus#fromCode(String)} throws for an unrecognised code. */
    @Test
    void claimStatusFromCodeThrowsForUnknownCode() {
        assertThatThrownBy(() -> ClaimStatus.fromCode("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UNKNOWN");
    }

    /** Verifies that {@link ClaimStatus} declares exactly 8 constants. */
    @Test
    void claimStatusHasAllRequiredValues() {
        assertThat(ClaimStatus.values()).hasSize(8);
    }

    /** Verifies that {@link DenialReasonCode} constants carry non-blank member-facing text. */
    @Test
    void denialReasonCodeHasMemberFacingText() {
        assertThat(DenialReasonCode.TIMELY.getMemberFacingText()).isNotBlank();
        assertThat(DenialReasonCode.DUP30.getMemberFacingText()).isNotBlank();
    }

    /** Verifies that {@link PlaceOfService#fromCode(String)} resolves numeric POS codes. */
    @Test
    void placeOfServiceFromCodeWorksWithNumericCodes() {
        assertThat(PlaceOfService.fromCode("11")).isEqualTo(PlaceOfService.OFFICE);
        assertThat(PlaceOfService.fromCode("22")).isEqualTo(PlaceOfService.OUTPATIENT_HOSPITAL);
    }
}
