package com.claimscore.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * Unit tests for DateUtil. No DB, no container.
 *
 * @author jkowalczyk
 * $Id: DateUtilTest.java,v 1.4 2013/03/18 14:02:11 jkowalczyk Exp $
 */
public class DateUtilTest {

    private static Date date(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, day);
        return cal.getTime();
    }

    @Test
    public void testAddDaysAcrossMonthEnd() {
        Date jan30 = date(2013, 1, 30);
        Date result = DateUtil.addDays(jan30, 3);
        assertEquals(date(2013, 2, 2), result);
    }

    @Test
    public void testAddDaysNegativeAcrossYearEnd() {
        Date jan2 = date(2013, 1, 2);
        assertEquals(date(2012, 12, 31), DateUtil.addDays(jan2, -2));
    }

    @Test
    public void testDaysBetweenSameDayIsZero() {
        Date d = date(2013, 3, 15);
        assertEquals(0, DateUtil.daysBetween(d, d));
    }

    @Test
    public void testDaysBetweenThirtyDaySpan() {
        Date from = date(2013, 3, 1);
        Date to = date(2013, 3, 31);
        assertEquals(30, DateUtil.daysBetween(from, to));
    }

    @Test
    public void testDaysBetweenAcrossDstChange() {
        // FIXME: INC-2210 - relies on the Math.round in daysBetween
        Date from = date(2013, 3, 1);
        Date to = date(2013, 4, 1);
        assertEquals(31, DateUtil.daysBetween(from, to));
    }

    @Test
    public void testPlanYearJuneIsPreviousYear() {
        assertEquals(2012, DateUtil.planYear(date(2013, 6, 15), 7));
    }

    @Test
    public void testPlanYearJulyIsSameYear() {
        assertEquals(2013, DateUtil.planYear(date(2013, 7, 1), 7));
    }

    @Test
    public void testPlanYearCalendarYearPlan() {
        assertEquals(2013, DateUtil.planYear(date(2013, 1, 1), 1));
        assertEquals(2013, DateUtil.planYear(date(2013, 12, 31), 1));
    }

    @Test
    public void testFormatNullIsEmpty() {
        assertEquals("", DateUtil.format(null));
    }

    @Test
    public void testFormatUsesDisplayPattern() {
        assertEquals("03/05/2013", DateUtil.format(date(2013, 3, 5)));
    }

    @Test
    public void testParseRoundTrip() throws Exception {
        Date parsed = DateUtil.parse("11/22/2012");
        assertNotNull(parsed);
        assertEquals(date(2012, 11, 22), parsed);
        assertEquals("11/22/2012", DateUtil.format(parsed));
    }

    @Test
    public void testTruncateDropsTimeOfDay() {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2013, Calendar.MARCH, 5, 17, 45, 30);
        assertEquals(date(2013, 3, 5), DateUtil.truncate(cal.getTime()));
        assertNull(DateUtil.truncate(null));
    }
}
