package com.claimscore.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * EJB 2.x home interface for AdjudicationService.
 *
 * @author dpreston
 * $Id: AdjudicationServiceHome.java,v 1.2 2008/09/19 13:10:41 dpreston Exp $
 */
public interface AdjudicationServiceHome extends EJBHome {

    AdjudicationService create() throws CreateException, RemoteException;
}
