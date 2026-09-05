package com.claimscore.util;

/**
 * Claim status codes. Values must match LK_CLAIM_STATUS.
 *
 * @author rmendez
 * $Id: ClaimStatus.java,v 1.6 2011/02/03 09:41:27 rmendez Exp $
 */
public final class ClaimStatus {

    public static final String RECEIVED = "REC";
    public static final String PENDING = "PND";
    public static final String APPROVED = "APR";
    public static final String PARTIAL = "PAR";
    public static final String DENIED = "DEN";
    public static final String PAID = "PAID";
    public static final String REOPENED = "REO";
    public static final String VOIDED = "VOID";

    private ClaimStatus() {
    }

    public static boolean isTerminal(String cd) {
        return PAID.equals(cd) || VOIDED.equals(cd);
    }

    public static boolean isAdjudicable(String cd) {
        return RECEIVED.equals(cd) || PENDING.equals(cd) || REOPENED.equals(cd);
    }

    public static boolean isReopenable(String cd) {
        return DENIED.equals(cd) || APPROVED.equals(cd) || PARTIAL.equals(cd);
    }

    // TODO: read names from LK_CLAIM_STATUS instead of duplicating them here (INC-3611)
    public static String displayName(String cd) {
        if (RECEIVED.equals(cd)) {
            return "Received";
        } else if (PENDING.equals(cd)) {
            return "Pending Review";
        } else if (APPROVED.equals(cd)) {
            return "Approved";
        } else if (PARTIAL.equals(cd)) {
            return "Partially Approved";
        } else if (DENIED.equals(cd)) {
            return "Denied";
        } else if (PAID.equals(cd)) {
            return "Paid";
        } else if (REOPENED.equals(cd)) {
            return "Reopened";
        } else if (VOIDED.equals(cd)) {
            return "Voided";
        } else {
            return cd;
        }
    }
}
