package com.claimscore.dao;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.claimscore.util.OracleConnectionFactory;

/**
 * Loads the LK_* code tables into a process-wide cache.
 *
 * @author jkowalczyk
 * $Id: LookupDAO.java,v 1.5 2010/04/12 08:15:39 jkowalczyk Exp $
 */
public class LookupDAO {

    private static final Logger log = Logger.getLogger(LookupDAO.class);

    // TODO: no way to refresh without a server bounce (INC-2877)
    private static final Hashtable<String, Map<String, String>> CACHE = new Hashtable<String, Map<String, String>>();

    public Map<String, String> loadLookup(String tableName, String codeColumn, String nameColumn)
            throws SQLException {
        Map<String, String> cached = CACHE.get(tableName);
        if (cached != null) {
            return cached;
        }
        Map<String, String> values = new LinkedHashMap<String, String>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT " + codeColumn + ", " + nameColumn + " FROM " + tableName
                + " WHERE ACTIVE_FLAG = 'Y' ORDER BY SORT_ORDER";
        try {
            conn = OracleConnectionFactory.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                values.put(rs.getString(1), rs.getString(2));
            }
        } finally {
            OracleConnectionFactory.close(rs, stmt, conn);
        }
        log.info("Cached " + values.size() + " rows from " + tableName);
        CACHE.put(tableName, values);
        return values;
    }

    public String statusName(String statusCode) throws SQLException {
        String name = loadLookup("LK_CLAIM_STATUS", "STATUS_CD", "STATUS_NAME").get(statusCode);
        return name == null ? statusCode : name;
    }

    public Map<String, String> denialReasons() throws SQLException {
        Map<String, String> cached = CACHE.get("LK_DENIAL_REASON");
        if (cached != null) {
            return cached;
        }
        Map<String, String> values = loadDenialReasons();
        CACHE.put("LK_DENIAL_REASON", values);
        return values;
    }

    private Map<String, String> loadDenialReasons() throws SQLException {
        Map<String, String> values = new LinkedHashMap<String, String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement("SELECT DENIAL_REASON_CD, SHORT_DESC FROM LK_DENIAL_REASON "
                    + "WHERE ACTIVE_FLAG = 'Y' ORDER BY DENIAL_REASON_CD");
            rs = ps.executeQuery();
            while (rs.next()) {
                values.put(rs.getString("DENIAL_REASON_CD"), rs.getString("SHORT_DESC"));
            }
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
        return values;
    }
}
