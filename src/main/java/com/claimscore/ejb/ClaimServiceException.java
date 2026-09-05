package com.claimscore.ejb;

import javax.ejb.ApplicationException;

/**
 * Checked business exception thrown by the ClaimsCore session beans.
 *
 * @author rmendez
 * $Id: ClaimServiceException.java,v 1.4 2010/06/17 11:20:03 rmendez Exp $
 */
@ApplicationException(rollback = true)
public class ClaimServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    private String errorCode;

    public ClaimServiceException(String message) {
        super(message);
    }

    public ClaimServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
