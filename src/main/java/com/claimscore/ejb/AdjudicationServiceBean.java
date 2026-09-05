package com.claimscore.ejb;

import java.sql.*;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.log4j.Logger;

import com.claimscore.dao.AuditLogDAO;
import com.claimscore.dao.ClaimDAOImpl;
import com.claimscore.dto.AdjudicationResultDTO;
import com.claimscore.dto.ClaimDTO;
import com.claimscore.util.OracleConnectionFactory;

/**
 * EJB 2.x stateless session bean fronting PKG_CLAIMS (container-managed transactions).
 *
 * @author dpreston
 * $Id: AdjudicationServiceBean.java,v 1.19 2013/11/06 15:22:41 dpreston Exp $
 */
public class AdjudicationServiceBean implements SessionBean {

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(AdjudicationServiceBean.class);

    private static final int ORA_INVALID_STATUS = 20001;

    private SessionContext ctx;

    public void ejbCreate() throws CreateException {
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void ejbRemove() {
    }

    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    public AdjudicationResultDTO adjudicate(Long claimId, String user) throws ClaimServiceException {
        Connection conn = null;
        CallableStatement cs = null;
        AdjudicationResultDTO result = new AdjudicationResultDTO();
        result.setClaimId(claimId);
        try {
            conn = OracleConnectionFactory.getConnection();
            cs = conn.prepareCall("{call PKG_CLAIMS.ADJUDICATE_CLAIM(?, ?, ?, ?)}");
            cs.setLong(1, claimId.longValue());
            cs.setString(2, user);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.execute();
            result.setResultCode(cs.getString(3));
            result.setMessage(cs.getString(4));
            new AuditLogDAO().log(conn, claimId, "ADJ", user, cs.getString(3) + ": " + cs.getString(4));
            ClaimDTO header = new ClaimDAOImpl().findById(claimId);
            result.setTotalAllowedAmount(header.getTotalAllowedAmount());
            result.setTotalPaidAmount(header.getTotalPaidAmount());
            result.setPatientLiabilityAmount(header.getPatientLiabilityAmount());
            result.setDeductibleAppliedAmount(header.getDeductibleAppliedAmount());
            result.setDenialReasonCode(header.getDenialReasonCode());
            result.setAdjudicatedDate(header.getAdjudicatedDate());
            return result;
        } catch (SQLException e) {
            throw translate(e, "Adjudication failed for claim " + claimId);
        } finally {
            OracleConnectionFactory.close(null, cs, conn);
        }
    }

    public void reopen(Long claimId, String user, String reason) throws ClaimServiceException {
        callWithReason("{call PKG_CLAIMS.REOPEN_CLAIM(?, ?, ?)}", "REO", claimId, user, reason);
    }

    public void voidClaim(Long claimId, String user, String reason) throws ClaimServiceException {
        callWithReason("{call PKG_CLAIMS.VOID_CLAIM(?, ?, ?)}", "VOID", claimId, user, reason);
    }

    private void callWithReason(String sql, String actionCode, Long claimId, String user, String reason)
            throws ClaimServiceException {
        Connection conn = null;
        CallableStatement cs = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            cs = conn.prepareCall(sql);
            cs.setLong(1, claimId.longValue());
            cs.setString(2, user);
            cs.setString(3, reason);
            cs.execute();
            new AuditLogDAO().log(conn, claimId, actionCode, user, reason);
        } catch (SQLException e) {
            throw translate(e, "Status change failed for claim " + claimId);
        } finally {
            OracleConnectionFactory.close(null, cs, conn);
        }
    }

    // TODO INC-4516: -20002 / -20003 still surface as the raw ORA- text
    private ClaimServiceException translate(SQLException e, String message) {
        if (e.getErrorCode() == ORA_INVALID_STATUS) {
            ClaimServiceException ex = new ClaimServiceException("Claim is not in an adjudicable status", e);
            ex.setErrorCode("INVALID_STATUS");
            return ex;
        }
        log.error(message + ": " + e.getMessage(), e);
        ctx.setRollbackOnly();
        ClaimServiceException ex = new ClaimServiceException(message, e);
        ex.setErrorCode("DB_ERROR");
        return ex;
    }
}
