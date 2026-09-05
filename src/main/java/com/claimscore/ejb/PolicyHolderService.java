package com.claimscore.ejb;

import java.util.List;

import javax.ejb.Local;

import com.claimscore.dto.PolicyHolderDTO;

/**
 * Local business interface for policy holder enquiry.
 *
 * @author jkowalczyk
 * $Id: PolicyHolderService.java,v 1.3 2009/11/24 14:07:55 jkowalczyk Exp $
 */
@Local
public interface PolicyHolderService {

    PolicyHolderDTO getPolicyHolder(Long policyHolderId) throws ClaimServiceException;

    PolicyHolderDTO findByMemberNumber(String memberNumber) throws ClaimServiceException;

    List<PolicyHolderDTO> search(String lastName, String memberNumber) throws ClaimServiceException;
}
