package com.claimscore.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.claimscore.dto.ClaimDTO;
import com.claimscore.dto.ClaimLineDTO;
import com.claimscore.util.DateUtil;
import com.claimscore.util.OracleConnectionFactory;

/**
 * Raw JDBC implementation of ClaimDAO against the CLAIMS schema.
 *
 * @author rmendez
 * $Id: ClaimDAOImpl.java,v 1.37 2016/02/11 10:05:48 pnakamura Exp $
 */
public class ClaimDAOImpl implements ClaimDAO {

    private static final Logger log = Logger.getLogger(ClaimDAOImpl.class);

    private static final String BASE_SELECT = "SELECT C.CLAIM_ID, C.CLAIM_NUM, C.PH_ID, P.MEMBER_NUM, "
            + "P.LAST_NAME || ', ' || P.FIRST_NAME AS PH_NAME, C.PLAN_ID, PL.PLAN_CODE, C.PROVIDER_ID, "
            + "PR.PROVIDER_NAME, C.CLAIM_TYPE_CD, C.STATUS_CD, S.STATUS_NAME, C.POS_CD, C.INCIDENT_DT, "
            + "C.RECEIVED_DT, C.ADJUDICATED_DT, C.TOTAL_BILLED_AMT, C.TOTAL_ALLOWED_AMT, C.TOTAL_PAID_AMT, "
            + "C.PATIENT_LIAB_AMT, C.DEDUCTIBLE_APPLIED_AMT, C.DENIAL_REASON_CD, C.VERSION_NO "
            + "FROM CLAIMS C JOIN POLICY_HOLDERS P ON C.PH_ID = P.PH_ID "
            + "JOIN PLANS PL ON C.PLAN_ID = PL.PLAN_ID "
            + "JOIN PROVIDERS PR ON C.PROVIDER_ID = PR.PROVIDER_ID "
            + "JOIN LK_CLAIM_STATUS S ON C.STATUS_CD = S.STATUS_CD";

    private ClaimLineDAO lineDAO = new ClaimLineDAO();

    public ClaimDTO findById(Long claimId) throws SQLException {
        return findOne(" WHERE C.CLAIM_ID = ?", claimId);
    }

    public ClaimDTO findByClaimNumber(String claimNumber) throws SQLException {
        return findOne(" WHERE C.CLAIM_NUM = ?", claimNumber);
    }

    private ClaimDTO findOne(String where, Object key) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement(BASE_SELECT + where);
            ps.setObject(1, key);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapHeader(rs);
            }
            return null;
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
    }

    public ClaimDTO getClaimWithLines(Long claimId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ClaimDTO claim = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement(BASE_SELECT + " WHERE C.CLAIM_ID = ?");
            ps.setLong(1, claimId.longValue());
            rs = ps.executeQuery();
            if (rs.next()) {
                claim = mapHeader(rs);
                claim.setLines(lineDAO.findByClaimId(conn, claimId));
            }
            return claim;
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
    }

    // TODO: pull the lines in one query, this gets slow for members with many claims (INC-4290)
    public List<ClaimDTO> findClaimsWithLines(Long policyHolderId) throws SQLException {
        List<ClaimDTO> claims = new ArrayList<ClaimDTO>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement(BASE_SELECT + " WHERE C.PH_ID = ? ORDER BY C.RECEIVED_DT DESC");
            ps.setLong(1, policyHolderId.longValue());
            rs = ps.executeQuery();
            while (rs.next()) {
                claims.add(mapHeader(rs));
            }
            rs.close();
            ps.close();
            for (ClaimDTO claim : claims) {
                ps = conn.prepareStatement("SELECT LINE_ID, CLAIM_ID, LINE_NO, SVC_CD, DIAG_CD, SVC_DT, UNITS, "
                        + "BILLED_AMT, ALLOWED_AMT, PAID_AMT, LINE_STATUS_CD, DENIAL_REASON_CD "
                        + "FROM CLAIM_LINES WHERE CLAIM_ID = ? ORDER BY LINE_NO");
                ps.setLong(1, claim.getClaimId().longValue());
                rs = ps.executeQuery();
                while (rs.next()) {
                    claim.addLine(mapLine(rs));
                }
                rs.close();
                ps.close();
            }
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
        return claims;
    }

    public List<ClaimDTO> search(String memberNumber, String statusCode, Date fromDate, Date toDate, int maxRows)
            throws SQLException {
        List<ClaimDTO> claims = new ArrayList<ClaimDTO>();
        StringBuffer sql = new StringBuffer(BASE_SELECT);
        sql.append(" WHERE 1 = 1");
        boolean byMember = memberNumber != null && memberNumber.trim().length() > 0;
        boolean byStatus = statusCode != null && statusCode.trim().length() > 0;
        if (byMember) {
            sql.append(" AND P.MEMBER_NUM = ?");
        }
        if (byStatus) {
            sql.append(" AND C.STATUS_CD = ?");
        }
        if (fromDate != null) {
            sql.append(" AND C.INCIDENT_DT >= ?");
        }
        if (toDate != null) {
            sql.append(" AND C.INCIDENT_DT <= ?");
        }
        sql.append(" AND ROWNUM <= ? ORDER BY C.RECEIVED_DT DESC");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (byMember) {
                ps.setString(idx++, memberNumber.trim());
            }
            if (byStatus) {
                ps.setString(idx++, statusCode.trim());
            }
            if (fromDate != null) {
                ps.setDate(idx++, DateUtil.toSqlDate(fromDate));
            }
            if (toDate != null) {
                ps.setDate(idx++, DateUtil.toSqlDate(toDate));
            }
            ps.setInt(idx, maxRows);
            rs = ps.executeQuery();
            while (rs.next()) {
                claims.add(mapHeader(rs));
            }
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
        return claims;
    }

    public Long insert(ClaimDTO claim) throws SQLException {
        Connection conn = null;
        Statement seqStmt = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        long claimId;
        try {
            conn = OracleConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            seqStmt = conn.createStatement();
            rs = seqStmt.executeQuery("SELECT SEQ_CLAIM.NEXTVAL FROM DUAL");
            rs.next();
            claimId = rs.getLong(1);
            rs.close();
            seqStmt.close();
            ps = conn.prepareStatement("INSERT INTO CLAIMS (CLAIM_ID, CLAIM_NUM, PH_ID, PLAN_ID, PROVIDER_ID, "
                    + "CLAIM_TYPE_CD, STATUS_CD, POS_CD, INCIDENT_DT, RECEIVED_DT, TOTAL_BILLED_AMT, "
                    + "VERSION_NO, CREATED_BY, CREATED_DT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, USER, SYSDATE)");
            ps.setLong(1, claimId);
            ps.setString(2, claim.getClaimNumber());
            ps.setLong(3, claim.getPolicyHolderId().longValue());
            ps.setLong(4, claim.getPlanId().longValue());
            ps.setLong(5, claim.getProviderId().longValue());
            ps.setString(6, claim.getClaimTypeCode());
            ps.setString(7, claim.getStatusCode());
            ps.setString(8, claim.getPlaceOfServiceCode());
            ps.setDate(9, DateUtil.toSqlDate(claim.getIncidentDate()));
            ps.setTimestamp(10, DateUtil.toTimestamp(claim.getReceivedDate()));
            ps.setBigDecimal(11, claim.getTotalBilledAmount());
            ps.executeUpdate();
            claim.setClaimId(new Long(claimId));
            for (ClaimLineDTO line : claim.getLines()) {
                line.setClaimId(new Long(claimId));
                lineDAO.insert(conn, line);
            }
            conn.commit();
        } catch (SQLException e) {
            log.error("insert failed, rolling back: " + e.getMessage());
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
            OracleConnectionFactory.close(rs, ps, conn);
        }
        return new Long(claimId);
    }

    public void updateStatus(Long claimId, String statusCode, String user) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement("UPDATE CLAIMS SET STATUS_CD = ?, UPDATED_BY = ?, UPDATED_DT = SYSDATE, "
                    + "VERSION_NO = VERSION_NO + 1 WHERE CLAIM_ID = ?");
            ps.setString(1, statusCode);
            ps.setString(2, user);
            ps.setLong(3, claimId.longValue());
            ps.executeUpdate();
        } finally {
            OracleConnectionFactory.close(null, ps, conn);
        }
    }

    public int countDuplicates(Long policyHolderId, Long providerId, Date incidentDate, int windowDays)
            throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM CLAIMS WHERE PH_ID = ? AND PROVIDER_ID = ? "
                    + "AND INCIDENT_DT BETWEEN ? AND ? AND STATUS_CD NOT IN ('DEN', 'VOID')");
            ps.setLong(1, policyHolderId.longValue());
            ps.setLong(2, providerId.longValue());
            ps.setDate(3, DateUtil.toSqlDate(DateUtil.addDays(incidentDate, -windowDays)));
            ps.setDate(4, DateUtil.toSqlDate(DateUtil.addDays(incidentDate, windowDays)));
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
    }

    private ClaimDTO mapHeader(ResultSet rs) throws SQLException {
        ClaimDTO c = new ClaimDTO();
        c.setClaimId(new Long(rs.getLong("CLAIM_ID")));
        c.setClaimNumber(rs.getString("CLAIM_NUM"));
        c.setPolicyHolderId(new Long(rs.getLong("PH_ID")));
        c.setMemberNumber(rs.getString("MEMBER_NUM"));
        c.setPolicyHolderName(rs.getString("PH_NAME"));
        c.setPlanId(new Long(rs.getLong("PLAN_ID")));
        c.setPlanCode(rs.getString("PLAN_CODE"));
        c.setProviderId(new Long(rs.getLong("PROVIDER_ID")));
        c.setProviderName(rs.getString("PROVIDER_NAME"));
        c.setClaimTypeCode(rs.getString("CLAIM_TYPE_CD"));
        c.setStatusCode(rs.getString("STATUS_CD"));
        c.setStatusName(rs.getString("STATUS_NAME"));
        c.setPlaceOfServiceCode(rs.getString("POS_CD"));
        Timestamp ts = rs.getTimestamp("INCIDENT_DT");
        c.setIncidentDate(ts == null ? null : new Date(ts.getTime()));
        ts = rs.getTimestamp("RECEIVED_DT");
        c.setReceivedDate(ts == null ? null : new Date(ts.getTime()));
        ts = rs.getTimestamp("ADJUDICATED_DT");
        c.setAdjudicatedDate(ts == null ? null : new Date(ts.getTime()));
        c.setTotalBilledAmount(rs.getBigDecimal("TOTAL_BILLED_AMT"));
        c.setTotalAllowedAmount(rs.getBigDecimal("TOTAL_ALLOWED_AMT"));
        c.setTotalPaidAmount(rs.getBigDecimal("TOTAL_PAID_AMT"));
        c.setPatientLiabilityAmount(rs.getBigDecimal("PATIENT_LIAB_AMT"));
        c.setDeductibleAppliedAmount(rs.getBigDecimal("DEDUCTIBLE_APPLIED_AMT"));
        c.setDenialReasonCode(rs.getString("DENIAL_REASON_CD"));
        c.setVersionNo(rs.getInt("VERSION_NO"));
        return c;
    }

    private ClaimLineDTO mapLine(ResultSet rs) throws SQLException {
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
