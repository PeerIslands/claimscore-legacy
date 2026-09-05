package com.claimscore.ejb;

import java.sql.SQLException;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.log4j.Logger;

import com.claimscore.dao.PolicyHolderDAO;
import com.claimscore.dao.PolicyHolderDAOImpl;
import com.claimscore.dto.PolicyHolderDTO;
import com.claimscore.util.Constants;

/**
 * Stateless session bean for policy holder enquiry (EJB 3.0, CMT).
 *
 * @author jkowalczyk
 * $Id: PolicyHolderServiceBean.java,v 1.7 2012/05/14 09:31:26 jkowalczyk Exp $
 */
@Stateless(name = "PolicyHolderService")
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PolicyHolderServiceBean implements PolicyHolderService {

    private static final Logger log = Logger.getLogger(PolicyHolderServiceBean.class);

    private PolicyHolderDAO policyHolderDAO = new PolicyHolderDAOImpl();

    public PolicyHolderDTO getPolicyHolder(Long policyHolderId) throws ClaimServiceException {
        try {
            PolicyHolderDTO ph = policyHolderDAO.findById(policyHolderId);
            if (ph == null) {
                throw fail("Policy holder not found: " + policyHolderId, "NOT_FOUND", null);
            }
            return ph;
        } catch (SQLException e) {
            throw fail("Unable to load policy holder " + policyHolderId, "DB_ERROR", e);
        }
    }

    public PolicyHolderDTO findByMemberNumber(String memberNumber) throws ClaimServiceException {
        try {
            PolicyHolderDTO ph = policyHolderDAO.findByMemberNumber(memberNumber);
            if (ph == null) {
                throw fail("Unknown member number: " + memberNumber, "NOT_FOUND", null);
            }
            return ph;
        } catch (SQLException e) {
            throw fail("Unable to load member " + memberNumber, "DB_ERROR", e);
        }
    }

    public List<PolicyHolderDTO> search(String lastName, String memberNumber) throws ClaimServiceException {
        try {
            return policyHolderDAO.search(lastName, memberNumber, Constants.MAX_SEARCH_ROWS);
        } catch (SQLException e) {
            throw fail("Policy holder search failed", "DB_ERROR", e);
        }
    }

    private ClaimServiceException fail(String message, String errorCode, SQLException cause) {
        if (cause != null) {
            log.error(message, cause);
        }
        ClaimServiceException ex = cause == null ? new ClaimServiceException(message)
                : new ClaimServiceException(message, cause);
        ex.setErrorCode(errorCode);
        return ex;
    }
}
