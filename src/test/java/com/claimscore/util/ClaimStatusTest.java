package com.claimscore.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for ClaimStatus code checks.
 *
 * @author rmendez
 * $Id: ClaimStatusTest.java,v 1.2 2011/02/03 09:58:10 rmendez Exp $
 */
public class ClaimStatusTest {

    @Test
    public void testIsTerminal() {
        assertTrue(ClaimStatus.isTerminal(ClaimStatus.PAID));
        assertTrue(ClaimStatus.isTerminal(ClaimStatus.VOIDED));
        assertFalse(ClaimStatus.isTerminal(ClaimStatus.PENDING));
        assertFalse(ClaimStatus.isTerminal(null));
    }

    @Test
    public void testIsAdjudicable() {
        assertTrue(ClaimStatus.isAdjudicable(ClaimStatus.RECEIVED));
        assertTrue(ClaimStatus.isAdjudicable(ClaimStatus.PENDING));
        assertTrue(ClaimStatus.isAdjudicable(ClaimStatus.REOPENED));
        assertFalse(ClaimStatus.isAdjudicable(ClaimStatus.APPROVED));
        assertFalse(ClaimStatus.isAdjudicable(ClaimStatus.PAID));
    }

    @Test
    public void testIsReopenable() {
        assertTrue(ClaimStatus.isReopenable(ClaimStatus.DENIED));
        assertTrue(ClaimStatus.isReopenable(ClaimStatus.APPROVED));
        assertTrue(ClaimStatus.isReopenable(ClaimStatus.PARTIAL));
        assertFalse(ClaimStatus.isReopenable(ClaimStatus.RECEIVED));
        assertFalse(ClaimStatus.isReopenable(ClaimStatus.VOIDED));
    }

    @Test
    public void testDisplayNameKnownCode() {
        assertEquals("Denied", ClaimStatus.displayName("DEN"));
        assertEquals("Partially Approved", ClaimStatus.displayName("PAR"));
    }

    @Test
    public void testDisplayNameUnknownCodeReturnsItself() {
        assertEquals("XYZ", ClaimStatus.displayName("XYZ"));
    }

    @Test
    public void testCodeValuesMatchLookupTable() {
        assertEquals("REC", ClaimStatus.RECEIVED);
        assertEquals("PND", ClaimStatus.PENDING);
        assertEquals("PAID", ClaimStatus.PAID);
        assertEquals("VOID", ClaimStatus.VOIDED);
    }
}
