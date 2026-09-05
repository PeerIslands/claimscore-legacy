package com.claimscore.ejb;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;

import com.claimscore.dto.AdjudicationResultDTO;

/**
 * EJB 2.x remote component interface for claim adjudication.
 *
 * @author dpreston
 * $Id: AdjudicationService.java,v 1.5 2009/04/08 08:55:12 dpreston Exp $
 */
public interface AdjudicationService extends EJBObject {

    AdjudicationResultDTO adjudicate(Long claimId, String user) throws RemoteException, ClaimServiceException;

    void reopen(Long claimId, String user, String reason) throws RemoteException, ClaimServiceException;

    void voidClaim(Long claimId, String user, String reason) throws RemoteException, ClaimServiceException;
}
