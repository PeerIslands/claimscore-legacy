package com.claimscore.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Unit tests for MoneyUtil.
 *
 * @author jkowalczyk
 * $Id: MoneyUtilTest.java,v 1.3 2011/01/07 10:12:40 jkowalczyk Exp $
 */
public class MoneyUtilTest {

    @Test
    public void testRoundHalfUp() {
        BigDecimal result = MoneyUtil.round(new BigDecimal("10.005"));
        assertEquals("10.01", result.toPlainString());
    }

    @Test
    public void testRoundSetsScaleTwo() {
        assertEquals("5.00", MoneyUtil.round(new BigDecimal("5")).toPlainString());
        assertEquals("0.00", MoneyUtil.round(null).toPlainString());
    }

    @Test
    public void testPctTwentyOfHundred() {
        BigDecimal result = MoneyUtil.pct(new BigDecimal("100"), new BigDecimal("20"));
        assertEquals("20.00", result.toPlainString());
        assertTrue(new BigDecimal("20").compareTo(result) == 0);
    }

    @Test
    public void testPctRoundsToCents() {
        BigDecimal result = MoneyUtil.pct(new BigDecimal("33.33"), new BigDecimal("20"));
        assertEquals("6.67", result.toPlainString());
    }

    @Test
    public void testParseStripsDollarAndComma() {
        BigDecimal result = MoneyUtil.parse("$1,234.50");
        assertEquals("1234.50", result.toPlainString());
        assertTrue(new BigDecimal("1234.5").compareTo(result) == 0);
    }

    @Test
    public void testParseBlankIsZero() {
        assertTrue(MoneyUtil.ZERO.compareTo(MoneyUtil.parse("")) == 0);
        assertTrue(MoneyUtil.ZERO.compareTo(MoneyUtil.parse("   ")) == 0);
        assertTrue(MoneyUtil.ZERO.compareTo(MoneyUtil.parse(null)) == 0);
    }

    @Test
    public void testAddNullSafe() {
        BigDecimal result = MoneyUtil.add(null, new BigDecimal("1.00"));
        assertEquals("1.00", result.toPlainString());
        assertTrue(MoneyUtil.ZERO.compareTo(MoneyUtil.add(null, null)) == 0);
    }

    @Test
    public void testNvl() {
        assertTrue(MoneyUtil.ZERO.compareTo(MoneyUtil.nvl(null)) == 0);
        BigDecimal amt = new BigDecimal("7.25");
        assertTrue(amt.compareTo(MoneyUtil.nvl(amt)) == 0);
    }

    @Test
    public void testFormatWithGrouping() {
        assertEquals("$1,234.50", MoneyUtil.format(new BigDecimal("1234.5")));
        assertEquals("$0.00", MoneyUtil.format(null));
    }
}
