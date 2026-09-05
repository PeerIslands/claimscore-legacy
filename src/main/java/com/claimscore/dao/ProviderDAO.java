package com.claimscore.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.claimscore.dto.ProviderDTO;
import com.claimscore.util.OracleConnectionFactory;

/**
 * Read-only JDBC access to PROVIDERS and PROVIDER_SPECIALTIES.
 *
 * @author pnakamura
 * $Id: ProviderDAO.java,v 1.7 2012/05/30 13:48:02 pnakamura Exp $
 */
public class ProviderDAO {

    private static final Logger log = Logger.getLogger(ProviderDAO.class);

    private static final String BASE_SELECT = "SELECT PROVIDER_ID, NPI, PROVIDER_NAME, PROVIDER_TYPE_CD, TAX_ID, "
            + "ADDR_LINE1, CITY, STATE_CD, ZIP, PHONE, STATUS_CD FROM PROVIDERS";

    public ProviderDTO findById(Long providerId) throws SQLException {
        return findOne(" WHERE PROVIDER_ID = ?", providerId);
    }

    public ProviderDTO findByNpi(String npi) throws SQLException {
        return findOne(" WHERE NPI = ?", npi);
    }

    private ProviderDTO findOne(String where, Object key) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProviderDTO p = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement(BASE_SELECT + where);
            ps.setObject(1, key);
            rs = ps.executeQuery();
            if (rs.next()) {
                p = mapProvider(rs);
            }
            rs.close();
            ps.close();
            if (p != null) {
                ps = conn.prepareStatement("SELECT SPECIALTY_CD FROM PROVIDER_SPECIALTIES WHERE PROVIDER_ID = ? "
                        + "ORDER BY IS_PRIMARY DESC, SPECIALTY_CD");
                ps.setLong(1, p.getProviderId().longValue());
                rs = ps.executeQuery();
                while (rs.next()) {
                    p.getSpecialtyCodes().add(rs.getString("SPECIALTY_CD"));
                }
            } else {
                log.debug("No provider for" + where + " [" + key + "]");
            }
            return p;
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
    }

    public List<String> findSpecialtyCodes(Long providerId) throws SQLException {
        List<String> codes = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement("SELECT SPECIALTY_CD FROM PROVIDER_SPECIALTIES WHERE PROVIDER_ID = ? "
                    + "ORDER BY IS_PRIMARY DESC, SPECIALTY_CD");
            ps.setLong(1, providerId.longValue());
            rs = ps.executeQuery();
            while (rs.next()) {
                codes.add(rs.getString("SPECIALTY_CD"));
            }
        } finally {
            OracleConnectionFactory.close(rs, ps, conn);
        }
        return codes;
    }

    private ProviderDTO mapProvider(ResultSet rs) throws SQLException {
        ProviderDTO p = new ProviderDTO();
        p.setProviderId(new Long(rs.getLong("PROVIDER_ID")));
        p.setNpi(rs.getString("NPI"));
        p.setProviderName(rs.getString("PROVIDER_NAME"));
        p.setProviderTypeCode(rs.getString("PROVIDER_TYPE_CD"));
        p.setTaxId(rs.getString("TAX_ID"));
        p.setAddressLine1(rs.getString("ADDR_LINE1"));
        p.setCity(rs.getString("CITY"));
        p.setStateCode(rs.getString("STATE_CD"));
        p.setZip(rs.getString("ZIP"));
        p.setPhone(rs.getString("PHONE"));
        p.setStatusCode(rs.getString("STATUS_CD"));
        return p;
    }
}
