package com.claimscore.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Money arithmetic and formatting helpers.
 *
 * @author jkowalczyk
 * $Id: MoneyUtil.java,v 1.5 2010/11/12 16:55:02 jkowalczyk Exp $
 */
public final class MoneyUtil {

    public static final BigDecimal ZERO = new BigDecimal("0.00");

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private MoneyUtil() {
    }

    public static BigDecimal round(BigDecimal amt) {
        if (amt == null) {
            return ZERO;
        }
        return amt.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal pct(BigDecimal amt, BigDecimal pct) {
        if (amt == null || pct == null) {
            return ZERO;
        }
        return amt.multiply(pct).divide(HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal nvl(BigDecimal amt) {
        return amt == null ? ZERO : amt;
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return nvl(a).add(nvl(b));
    }

    public static String format(BigDecimal amt) {
        DecimalFormat fmt = new DecimalFormat("$#,##0.00");
        return fmt.format(nvl(amt));
    }

    public static BigDecimal parse(String s) {
        if (s == null) {
            return ZERO;
        }
        String cleaned = s.trim();
        if (cleaned.length() == 0) {
            return ZERO;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (c != '$' && c != ',') {
                sb.append(c);
            }
        }
        return round(new BigDecimal(sb.toString()));
    }
}
