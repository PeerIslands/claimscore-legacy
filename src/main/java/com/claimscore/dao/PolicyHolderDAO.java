package com.claimscore.dao;

import java.sql.SQLException;
import java.util.List;

import com.claimscore.dto.AddressDTO;
import com.claimscore.dto.PolicyHolderDTO;

/**
 * Data access contract for POLICY_HOLDERS and PH_ADDRESSES.
 *
 * @author jkowalczyk
 * $Id: PolicyHolderDAO.java,v 1.3 2009/11/24 14:07:55 jkowalczyk Exp $
 */
public interface PolicyHolderDAO {

    PolicyHolderDTO findById(Long policyHolderId) throws SQLException;

    PolicyHolderDTO findByMemberNumber(String memberNumber) throws SQLException;

    List<PolicyHolderDTO> search(String lastName, String memberNumber, int maxRows) throws SQLException;

    List<AddressDTO> findAddresses(Long policyHolderId) throws SQLException;
}
