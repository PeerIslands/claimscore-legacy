package com.claimscore.util;

/**
 * Application-wide constants for ClaimsCore.
 *
 * @author rmendez
 * $Id: Constants.java,v 1.14 2013/06/18 15:02:11 rmendez Exp $
 */
public interface Constants {

    String JNDI_DATASOURCE = "java:comp/env/jdbc/ClaimsCoreDS";
    String JNDI_ADJUDICATION_HOME = "ejb/AdjudicationServiceHome";
    String JNDI_CLAIM_SERVICE = "java:comp/env/ejb/ClaimService";
    String JNDI_POLICY_HOLDER_SERVICE = "java:comp/env/ejb/PolicyHolderService";

    // FIXME: remove once every environment has the datasource configured (CR-2013-088)
    String FALLBACK_JDBC_URL = "jdbc:oracle:thin:@clmdb01.larkspur.local:1521:CLMPRD";
    String FALLBACK_DB_USER = "CLAIMSCORE";
    String FALLBACK_DB_PASSWORD = "cl41msc0re";

    int DUPLICATE_WINDOW_DAYS = 30;
    int REOPEN_WINDOW_DAYS = 90;
    int TIMELY_FILING_DAYS = 365;
    int HIGH_DOLLAR_THRESHOLD = 25000;
    int MAX_SEARCH_ROWS = 200;

    String SESSION_USER_KEY = "claimscore.user";
    String SESSION_ROLES_KEY = "claimscore.roles";

    String DATE_FORMAT = "MM/dd/yyyy";
}
