package com.claimscore.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.claimscore.dto.AddressDTO;
import com.claimscore.dto.PolicyHolderDTO;
import com.claimscore.util.OracleConnectionFactory;

/**
 * Raw JDBC implementation of PolicyHolderDAO.
 *
 * @author jkowalczyk
 * $Id: PolicyHolderDAOImpl.java,v 1.19 2014/08/05 09:33:20 pnakamura Exp $
 */
public class PolicyHolderDAOImpl implements PolicyHolderDAO {

    private static final Logger log = Logger.getLogger(PolicyHolderDAOImpl.class);

    private static final String BASE_SELECT = "SELECT PH_ID, MEMBER_NUM, FIRST_NAME, LAST_NAME, MIDDLE_INIT, "
            + "BIRTH_DT, GENDER_CD, SSN_LAST4, STATUS_CD FROM POLICY_HOLDERS";

    private static final String ADDRESS_SELECT = "SELECT ADDR_ID, ADDR_TYPE_CD, LINE1, LINE2, CITY, STATE_CD, "
            + "ZIP, EFFECTIVE_DT, END_DT FROM PH_ADDRESSES WHERE PH_ID = ? ORDER BY EFFECTIVE_DT DESC";

    private static final String ENROLLMENT_SELECT = "SELECT E.PLAN_ID, PL.PLAN_CODE FROM PH_PLAN_ENROLLMENTS E "
            + "JOIN PLANS PL ON E.PLAN_ID = PL.PLAN_ID WHERE E.PH_ID = ? "
            + "AND (E.TERM_DT IS NULL OR E.TERM_DT >= SYSDATE) ORDER BY E.EFFECTIVE_DT DESC";

    public PolicyHolderDTO findById(Long policyHolderId) throws SQLException {
        return findOne(" WHERE PH_ID = ?", policyHolderId);
    }

    public PolicyHolderDTO findByMemberNumber(String memberNumber) throws SQLException {
        return findOne(" WHERE MEMBER_NUM = ?", memberNumber);
    }

    private PolicyHolderDTO findOne(String where, Object key) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PolicyHolderDTO ph = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement(BASE_SELECT + where);
            ps.setObject(1, key);
            rs = ps.executeQuery();
            if (rs.next()) {
                ph = mapPolicyHolder(rs);
            }
            rs.close();
            ps.close();
            if (ph == null) {
                return null;
            }
            ps = conn.prepareStatement(ENROLLMENT_SELECT);
            ps.setLong(1, ph.getPolicyHolderId().longValue());
            rs = ps.executeQuery();
            if (rs.next()) {
                ph.setActivePlanId(new Long(rs.getLong("PLAN_ID")));
                ph.setActivePlanCode(rs.getString("PLAN_CODE"));
            } else {
                log.debug("No active enrollment for PH_ID " + ph.getPolicyHolderId());
            }
            rs.close();
            ps.close();
            ps = conn.prepareStatement(ADDRESS_SELECT);
            ps.setLong(1, ph.getPolicyHolderId().longValue());
            rs = ps.executeQuery();
            while (rs.next()) {
                ph.getAddresses().add(mapAddress(rs));
            }
            return ph;
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
    }

    public List<PolicyHolderDTO> search(String lastName, String memberNumber, int maxRows) throws SQLException {
        List<PolicyHolderDTO> results = new ArrayList<PolicyHolderDTO>();
        boolean byName = lastName != null && lastName.trim().length() > 0;
        boolean byMember = memberNumber != null && memberNumber.trim().length() > 0;
        StringBuffer sql = new StringBuffer(BASE_SELECT);
        sql.append(" WHERE ROWNUM <= ?");
        if (byName) {
            sql.append(" AND UPPER(LAST_NAME) LIKE UPPER(?) || '%'");
        }
        if (byMember) {
            sql.append(" AND MEMBER_NUM = ?");
        }
        sql.append(" ORDER BY LAST_NAME, FIRST_NAME");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            ps.setInt(idx++, maxRows);
            if (byName) {
                ps.setString(idx++, lastName.trim());
            }
            if (byMember) {
                ps.setString(idx++, memberNumber.trim());
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                results.add(mapPolicyHolder(rs));
            }
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
        return results;
    }

    public List<AddressDTO> findAddresses(Long policyHolderId) throws SQLException {
        List<AddressDTO> addresses = new ArrayList<AddressDTO>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement(ADDRESS_SELECT);
            ps.setLong(1, policyHolderId.longValue());
            rs = ps.executeQuery();
            while (rs.next()) {
                addresses.add(mapAddress(rs));
            }
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
        return addresses;
    }

    private PolicyHolderDTO mapPolicyHolder(ResultSet rs) throws SQLException {
        PolicyHolderDTO ph = new PolicyHolderDTO();
        ph.setPolicyHolderId(new Long(rs.getLong("PH_ID")));
        ph.setMemberNumber(rs.getString("MEMBER_NUM"));
        ph.setFirstName(rs.getString("FIRST_NAME"));
        ph.setLastName(rs.getString("LAST_NAME"));
        ph.setMiddleInitial(rs.getString("MIDDLE_INIT"));
        Timestamp ts = rs.getTimestamp("BIRTH_DT");
        ph.setBirthDate(ts == null ? null : new Date(ts.getTime()));
        ph.setGenderCode(rs.getString("GENDER_CD"));
        ph.setSsnLast4(rs.getString("SSN_LAST4"));
        ph.setStatusCode(rs.getString("STATUS_CD"));
        return ph;
    }

    private AddressDTO mapAddress(ResultSet rs) throws SQLException {
        AddressDTO a = new AddressDTO();
        a.setAddressId(new Long(rs.getLong("ADDR_ID")));
        a.setAddressTypeCode(rs.getString("ADDR_TYPE_CD"));
        a.setLine1(rs.getString("LINE1"));
        a.setLine2(rs.getString("LINE2"));
        a.setCity(rs.getString("CITY"));
        a.setStateCode(rs.getString("STATE_CD"));
        a.setZip(rs.getString("ZIP"));
        Timestamp ts = rs.getTimestamp("EFFECTIVE_DT");
        a.setEffectiveDate(ts == null ? null : new Date(ts.getTime()));
        ts = rs.getTimestamp("END_DT");
        a.setEndDate(ts == null ? null : new Date(ts.getTime()));
        return a;
    }
}
