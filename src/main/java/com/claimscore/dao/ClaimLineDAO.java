package com.claimscore.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.claimscore.dto.ClaimLineDTO;
import com.claimscore.util.DateUtil;

/**
 * JDBC access to CLAIM_LINES. Always runs on a caller-supplied connection.
 *
 * @author rmendez
 * $Id: ClaimLineDAO.java,v 1.8 2012/01/19 16:22:03 rmendez Exp $
 */
public class ClaimLineDAO {

    private static final Logger log = Logger.getLogger(ClaimLineDAO.class);

    private static final String LINE_SELECT = "SELECT LINE_ID, CLAIM_ID, LINE_NO, SVC_CD, DIAG_CD, SVC_DT, UNITS, "
            + "BILLED_AMT, ALLOWED_AMT, PAID_AMT, LINE_STATUS_CD, DENIAL_REASON_CD FROM CLAIM_LINES";

    public List<ClaimLineDTO> findByClaimId(Connection conn, Long claimId) throws SQLException {
        List<ClaimLineDTO> lines = new ArrayList<ClaimLineDTO>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(LINE_SELECT + " WHERE CLAIM_ID = ? ORDER BY LINE_NO");
            ps.setLong(1, claimId.longValue());
            rs = ps.executeQuery();
            while (rs.next()) {
                lines.add(mapLine(rs));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return lines;
    }

    public Long insert(Connection conn, ClaimLineDTO line) throws SQLException {
        Statement seqStmt = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        long lineId;
        try {
            seqStmt = conn.createStatement();
            rs = seqStmt.executeQuery("SELECT SEQ_CLAIM_LINE.NEXTVAL FROM DUAL");
            rs.next();
            lineId = rs.getLong(1);
            rs.close();
            seqStmt.close();
            ps = conn.prepareStatement("INSERT INTO CLAIM_LINES (LINE_ID, CLAIM_ID, LINE_NO, SVC_CD, DIAG_CD, "
                    + "SVC_DT, UNITS, BILLED_AMT, LINE_STATUS_CD) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PND')");
            ps.setLong(1, lineId);
            ps.setLong(2, line.getClaimId().longValue());
            ps.setInt(3, line.getLineNumber());
            ps.setString(4, line.getServiceCode());
            ps.setString(5, line.getDiagnosisCode());
            ps.setDate(6, DateUtil.toSqlDate(line.getServiceDate()));
            ps.setInt(7, line.getUnits() > 0 ? line.getUnits() : 1);
            ps.setBigDecimal(8, line.getBilledAmount());
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        line.setLineId(new Long(lineId));
        if (log.isDebugEnabled()) {
            log.debug("Inserted CLAIM_LINES row " + lineId + " for claim " + line.getClaimId());
        }
        return new Long(lineId);
    }

    public void updateAmounts(Connection conn, Long lineId, BigDecimal allowed, BigDecimal paid,
            String lineStatusCode) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE CLAIM_LINES SET ALLOWED_AMT = ?, PAID_AMT = ?, LINE_STATUS_CD = ? "
                    + "WHERE LINE_ID = ?");
            ps.setBigDecimal(1, allowed);
            ps.setBigDecimal(2, paid);
            ps.setString(3, lineStatusCode);
            ps.setLong(4, lineId.longValue());
            int n = ps.executeUpdate();
            if (n == 0) {
                log.warn("updateAmounts touched no rows for LINE_ID " + lineId);
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    static ClaimLineDTO mapLine(ResultSet rs) throws SQLException {
        ClaimLineDTO line = new ClaimLineDTO();
        line.setLineId(new Long(rs.getLong("LINE_ID")));
        line.setClaimId(new Long(rs.getLong("CLAIM_ID")));
        line.setLineNumber(rs.getInt("LINE_NO"));
        line.setServiceCode(rs.getString("SVC_CD"));
        line.setDiagnosisCode(rs.getString("DIAG_CD"));
        Timestamp ts = rs.getTimestamp("SVC_DT");
        line.setServiceDate(ts == null ? null : new Date(ts.getTime()));
        line.setUnits(rs.getInt("UNITS"));
        line.setBilledAmount(rs.getBigDecimal("BILLED_AMT"));
        line.setAllowedAmount(rs.getBigDecimal("ALLOWED_AMT"));
        line.setPaidAmount(rs.getBigDecimal("PAID_AMT"));
        line.setLineStatusCode(rs.getString("LINE_STATUS_CD"));
        line.setDenialReasonCode(rs.getString("DENIAL_REASON_CD"));
        return line;
    }
}
