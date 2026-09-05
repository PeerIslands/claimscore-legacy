package com.claimscore.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.claimscore.dto.ClaimDTO;

/**
 * Local business interface for claim intake and enquiry.
 *
 * @author rmendez
 * $Id: ClaimService.java,v 1.9 2011/08/30 16:02:44 rmendez Exp $
 */
@Local
public interface ClaimService {

    ClaimDTO getClaim(Long claimId) throws ClaimServiceException;

    ClaimDTO getClaimByNumber(String claimNumber) throws ClaimServiceException;

    List<ClaimDTO> searchClaims(String memberNumber, String statusCode, Date fromDate, Date toDate)
            throws ClaimServiceException;

    List<ClaimDTO> getClaimsForPolicyHolder(Long policyHolderId) throws ClaimServiceException;

    Long submitClaim(ClaimDTO claim, String user) throws ClaimServiceException;

    void addNote(Long claimId, String noteTypeCode, String noteText, String user) throws ClaimServiceException;
}
