package com.claimscore.struts;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.claimscore.dao.AuditLogDAO;
import com.claimscore.dto.AdjudicationResultDTO;
import com.claimscore.dto.ClaimDTO;
import com.claimscore.ejb.AdjudicationService;
import com.claimscore.ejb.AdjudicationServiceHome;
import com.claimscore.ejb.ClaimService;
import com.claimscore.ejb.ClaimServiceException;
import com.claimscore.util.Constants;

/**
 * Claim view / submit / adjudicate / reopen, dispatched on the "method" parameter.
 *
 * @author mferrante
 * $Id: ClaimAction.java,v 1.27 2014/01/22 13:40:09 rmendez Exp $
 */
public class ClaimAction extends Action {

    private static final Logger log = Logger.getLogger(ClaimAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute(Constants.SESSION_USER_KEY);
        if (user == null) {
            return mapping.findForward("login");
        }
        String method = request.getParameter("method");
        try {
            if ("view".equals(method)) {
                return view(mapping, request, Long.valueOf(request.getParameter("claimId")));
            } else if ("submit".equals(method)) {
                return submit(mapping, (ClaimForm) form, request, user);
            } else if ("adjudicate".equals(method)) {
                Long claimId = Long.valueOf(request.getParameter("claimId"));
                AdjudicationResultDTO result = lookupAdjudicationService().adjudicate(claimId, user);
                log.info("Claim " + claimId + " adjudicated by " + user + ": " + result.getResultCode());
                request.setAttribute("result", result);
                return view(mapping, request, claimId);
            } else if ("reopen".equals(method)) {
                Long claimId = Long.valueOf(request.getParameter("claimId"));
                lookupAdjudicationService().reopen(claimId, user, request.getParameter("reason"));
                return view(mapping, request, claimId);
            }
            return mapping.findForward("submit");
        } catch (ClaimServiceException e) {
            return serviceError(mapping, request, e.getMessage(), e);
        } catch (RemoteException e) {
            return serviceError(mapping, request, e.getMessage(), e);
        }
    }

    private ActionForward view(ActionMapping mapping, HttpServletRequest request, Long claimId)
            throws Exception {
        ClaimDTO claim = lookupClaimService().getClaim(claimId);
        if (claim == null) {
            return serviceError(mapping, request, "Claim " + claimId + " was not found", null);
        }
        request.setAttribute("claim", claim);
        List<Map<String, Object>> auditTrail = new AuditLogDAO().findByClaimId(claimId);
        request.setAttribute("auditTrail", auditTrail);
        return mapping.findForward("view");
    }

    private ActionForward submit(ActionMapping mapping, ClaimForm form, HttpServletRequest request, String user)
            throws Exception {
        ActionErrors errors = form.validate(mapping, request);
        if (errors != null && !errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("submit");
        }
        Long id = lookupClaimService().submitClaim(form.toClaimDTO(), user);
        log.info("Claim " + id + " submitted by " + user);
        return new ActionForward("/claim.do?method=view&claimId=" + id, true);
    }

    private ActionForward serviceError(ActionMapping mapping, HttpServletRequest request, String message,
            Exception e) {
        log.error("Claim action failed: " + message, e);
        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.claim.service", message));
        saveErrors(request, errors);
        return mapping.findForward("error");
    }

    // TODO: cache the home in a servlet-context attribute (INC-3355)
    private ClaimService lookupClaimService() throws NamingException {
        InitialContext ctx = new InitialContext();
        return (ClaimService) ctx.lookup(Constants.JNDI_CLAIM_SERVICE);
    }

    private AdjudicationService lookupAdjudicationService() throws NamingException, CreateException,
            RemoteException {
        InitialContext ctx = new InitialContext();
        AdjudicationServiceHome home = (AdjudicationServiceHome) ctx.lookup(Constants.JNDI_ADJUDICATION_HOME);
        return home.create();
    }
}
