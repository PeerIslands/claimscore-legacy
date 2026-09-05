package com.claimscore.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.claimscore.dto.ClaimDTO;

/**
 * Data access contract for CLAIMS and their lines.
 *
 * @author rmendez
 * $Id: ClaimDAO.java,v 1.6 2011/03/02 09:41:17 rmendez Exp $
 */
public interface ClaimDAO {

    ClaimDTO findById(Long claimId) throws SQLException;

    ClaimDTO findByClaimNumber(String claimNumber) throws SQLException;

    ClaimDTO getClaimWithLines(Long claimId) throws SQLException;

    List<ClaimDTO> findClaimsWithLines(Long policyHolderId) throws SQLException;

    List<ClaimDTO> search(String memberNumber, String statusCode, Date fromDate, Date toDate, int maxRows)
            throws SQLException;

    Long insert(ClaimDTO claim) throws SQLException;

    void updateStatus(Long claimId, String statusCode, String user) throws SQLException;

    int countDuplicates(Long policyHolderId, Long providerId, Date incidentDate, int windowDays)
            throws SQLException;
}
