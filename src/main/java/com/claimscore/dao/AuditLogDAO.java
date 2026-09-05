package com.claimscore.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.claimscore.util.OracleConnectionFactory;

/**
 * Application-side writer and reader for CLAIM_AUDIT_LOG.
 *
 * @author rmendez
 * $Id: AuditLogDAO.java,v 1.4 2011/09/08 17:26:51 rmendez Exp $
 */
public class AuditLogDAO {

    private static final Logger log = Logger.getLogger(AuditLogDAO.class);

    // FIXME: TRG_CLAIM_AUDIT also writes STS/PAY rows, so status changes show up twice (INC-4102)
    public void log(Connection conn, Long claimId, String actionCode, String user, String detail)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("INSERT INTO CLAIM_AUDIT_LOG (AUDIT_ID, CLAIM_ID, ACTION_CD, CHANGED_BY, "
                    + "CHANGED_DT, DETAIL) VALUES (SEQ_AUDIT.NEXTVAL, ?, ?, ?, SYSDATE, ?)");
            ps.setLong(1, claimId.longValue());
            ps.setString(2, actionCode);
            ps.setString(3, user);
            ps.setString(4, detail == null ? null : (detail.length() > 1000 ? detail.substring(0, 1000) : detail));
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Audit " + actionCode + " on claim " + claimId + " by " + user);
        }
    }

    public List<Map<String, Object>> findByClaimId(Long claimId) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement("SELECT AUDIT_ID, CLAIM_ID, ACTION_CD, OLD_STATUS_CD, NEW_STATUS_CD, "
                    + "OLD_PAID_AMT, NEW_PAID_AMT, CHANGED_BY, CHANGED_DT, OS_USER, TERMINAL, DETAIL "
                    + "FROM CLAIM_AUDIT_LOG WHERE CLAIM_ID = ? ORDER BY CHANGED_DT DESC, AUDIT_ID DESC");
            ps.setLong(1, claimId.longValue());
            rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<String, Object>();
                for (int i = 1; i <= cols; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                rows.add(row);
            }
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
        return rows;
    }
}
