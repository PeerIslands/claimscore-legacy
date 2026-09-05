package com.claimscore.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date helpers shared by the web and EJB tiers.
 *
 * @author jkowalczyk
 * $Id: DateUtil.java,v 1.9 2012/09/27 11:18:44 jkowalczyk Exp $
 */
public final class DateUtil {

    private static final SimpleDateFormat DISPLAY_FMT = new SimpleDateFormat("MM/dd/yyyy");

    private static final long MILLIS_PER_DAY = 86400000L;

    private DateUtil() {
    }

    public static Date truncate(Date d) {
        if (d == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date addDays(Date d, int days) {
        if (d == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    // TODO: DST days come out as 23/25 hours, rounding papers over it (INC-2210)
    public static int daysBetween(Date from, Date to) {
        long diff = truncate(to).getTime() - truncate(from).getTime();
        return (int) Math.round((double) diff / MILLIS_PER_DAY);
    }

    public static String format(Date d) {
        if (d == null) {
            return "";
        }
        return DISPLAY_FMT.format(d);
    }

    public static Date parse(String s) throws ParseException {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        DISPLAY_FMT.setLenient(false);
        return DISPLAY_FMT.parse(s.trim());
    }

    public static java.sql.Date toSqlDate(Date d) {
        if (d == null) {
            return null;
        }
        return new java.sql.Date(d.getTime());
    }

    public static java.sql.Timestamp toTimestamp(Date d) {
        if (d == null) {
            return null;
        }
        return new java.sql.Timestamp(d.getTime());
    }

    public static int planYear(Date d, int startMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        if (month < startMonth) {
            year = year - 1;
        }
        return year;
    }
}
