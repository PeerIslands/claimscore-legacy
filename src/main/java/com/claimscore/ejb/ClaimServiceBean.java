package com.claimscore.ejb;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.log4j.Logger;

import com.claimscore.dao.ClaimDAO;
import com.claimscore.dao.ClaimDAOImpl;
import com.claimscore.dao.PolicyHolderDAO;
import com.claimscore.dao.PolicyHolderDAOImpl;
import com.claimscore.dao.ProviderDAO;
import com.claimscore.dto.ClaimDTO;
import com.claimscore.dto.ClaimLineDTO;
import com.claimscore.dto.PolicyHolderDTO;
import com.claimscore.util.ClaimStatus;
import com.claimscore.util.Constants;
import com.claimscore.util.MoneyUtil;
import com.claimscore.util.OracleConnectionFactory;

/**
 * Stateless session bean for claim intake and enquiry (EJB 3.0, CMT).
 *
 * @author rmendez
 * $Id: ClaimServiceBean.java,v 1.22 2016/02/11 10:05:48 pnakamura Exp $
 */
@Stateless(name = "ClaimService")
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ClaimServiceBean implements ClaimService {

    private static final Logger log = Logger.getLogger(ClaimServiceBean.class);

    private ClaimDAO claimDAO = new ClaimDAOImpl();
    private PolicyHolderDAO policyHolderDAO = new PolicyHolderDAOImpl();
    private ProviderDAO providerDAO = new ProviderDAO();

    public ClaimDTO getClaim(Long claimId) throws ClaimServiceException {
        try {
            return claimDAO.getClaimWithLines(claimId);
        } catch (SQLException e) {
            throw fail("Unable to load claim " + claimId, "DB_ERROR", e);
        }
    }

    public ClaimDTO getClaimByNumber(String claimNumber) throws ClaimServiceException {
        try {
            return claimDAO.findByClaimNumber(claimNumber);
        } catch (SQLException e) {
            throw fail("Unable to load claim " + claimNumber, "DB_ERROR", e);
        }
    }

    public List<ClaimDTO> searchClaims(String memberNumber, String statusCode, Date fromDate, Date toDate)
            throws ClaimServiceException {
        try {
            return claimDAO.search(memberNumber, statusCode, fromDate, toDate, Constants.MAX_SEARCH_ROWS);
        } catch (SQLException e) {
            throw fail("Claim search failed", "DB_ERROR", e);
        }
    }

    public List<ClaimDTO> getClaimsForPolicyHolder(Long policyHolderId) throws ClaimServiceException {
        try {
            return claimDAO.findClaimsWithLines(policyHolderId);
        } catch (SQLException e) {
            throw fail("Unable to load claims for policy holder " + policyHolderId, "DB_ERROR", e);
        }
    }

    public Long submitClaim(ClaimDTO claim, String user) throws ClaimServiceException {
        if (claim.getProviderId() == null) {
            throw fail("Provider is required", "PROVIDER_REQUIRED", null);
        }
        try {
            PolicyHolderDTO member = policyHolderDAO.findByMemberNumber(claim.getMemberNumber());
            if (member == null) {
                throw fail("Unknown member number: " + claim.getMemberNumber(), "MEMBER_NOT_FOUND", null);
            }
            if (member.getActivePlanId() == null) {
                throw fail("Member has no active enrollment: " + claim.getMemberNumber(), "NO_ENROLLMENT", null);
            }
            if (providerDAO.findById(claim.getProviderId()) == null) {
                throw fail("Unknown provider id: " + claim.getProviderId(), "PROVIDER_NOT_FOUND", null);
            }
            int dups = claimDAO.countDuplicates(member.getPolicyHolderId(), claim.getProviderId(),
                    claim.getIncidentDate(), Constants.DUPLICATE_WINDOW_DAYS);
            if (dups > 0) {
                log.warn("Possible duplicate for member " + claim.getMemberNumber() + " within "
                        + Constants.DUPLICATE_WINDOW_DAYS + " days, submitting anyway (INC-4118)");
            }
            claim.setPolicyHolderId(member.getPolicyHolderId());
            claim.setPlanId(member.getActivePlanId());
            claim.setStatusCode(ClaimStatus.RECEIVED);
            claim.setReceivedDate(new Date());
            BigDecimal total = MoneyUtil.ZERO;
            for (ClaimLineDTO line : claim.getLines()) {
                total = MoneyUtil.add(total, line.getBilledAmount());
            }
            claim.setTotalBilledAmount(total);
            // FIXME CR-2013-088: number comes from the clock, not SEQ_CLAIM, so a fast double-submit can collide
            String tail = "00000000" + (System.currentTimeMillis() % 100000000L);
            claim.setClaimNumber("CLM" + Calendar.getInstance().get(Calendar.YEAR) + "-"
                    + tail.substring(tail.length() - 8));
            return claimDAO.insert(claim);
        } catch (SQLException e) {
            throw fail("Claim submission failed", "DB_ERROR", e);
        }
    }

    public void addNote(Long claimId, String noteTypeCode, String noteText, String user)
            throws ClaimServiceException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = OracleConnectionFactory.getConnection();
            ps = conn.prepareStatement("INSERT INTO CLAIM_NOTES (NOTE_ID, CLAIM_ID, NOTE_TYPE_CD, NOTE_TEXT, "
                    + "CREATED_BY, CREATED_DT) VALUES (SEQ_CLAIM_NOTE.NEXTVAL, ?, ?, ?, ?, SYSDATE)");
            ps.setLong(1, claimId.longValue());
            ps.setString(2, noteTypeCode);
            String text = noteText == null ? "" : noteText;
            ps.setCharacterStream(3, new StringReader(text), text.length());
            ps.setString(4, user);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw fail("Unable to add note to claim " + claimId, "DB_ERROR", e);
        } finally {
            OracleConnectionFactory.close(null, ps, conn);
        }
    }

    private ClaimServiceException fail(String message, String errorCode, SQLException cause) {
        log.error(message, cause);
        ClaimServiceException ex = new ClaimServiceException(message, cause);
        ex.setErrorCode(errorCode);
        return ex;
    }
}
