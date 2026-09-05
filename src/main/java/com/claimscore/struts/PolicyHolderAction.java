package com.claimscore.struts;

import java.util.List;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.claimscore.dto.ClaimDTO;
import com.claimscore.dto.PolicyHolderDTO;
import com.claimscore.ejb.ClaimService;
import com.claimscore.ejb.ClaimServiceException;
import com.claimscore.ejb.PolicyHolderService;
import com.claimscore.util.Constants;

/**
 * Policy holder view and search; the view also lists the member's claims.
 *
 * @author mferrante
 * $Id: PolicyHolderAction.java,v 1.9 2012/05/14 09:31:26 jkowalczyk Exp $
 */
public class PolicyHolderAction extends Action {

    private static final Logger log = Logger.getLogger(PolicyHolderAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute(Constants.SESSION_USER_KEY) == null) {
            return mapping.findForward("login");
        }
        String method = request.getParameter("method");
        try {
            InitialContext ctx = new InitialContext();
            PolicyHolderService phService = (PolicyHolderService) ctx.lookup(Constants.JNDI_POLICY_HOLDER_SERVICE);
            ClaimService claimService = (ClaimService) ctx.lookup(Constants.JNDI_CLAIM_SERVICE);

            PolicyHolderDTO policyHolder = null;
            if ("search".equals(method)) {
                String lastName = request.getParameter("lastName");
                String memberNumber = request.getParameter("memberNumber");
                List<PolicyHolderDTO> matches = phService.search(lastName, memberNumber);
                request.setAttribute("policyHolders", matches);
                if (matches.size() > 0) {
                    policyHolder = matches.get(0);
                }
                log.debug("Policy holder search '" + lastName + "'/'" + memberNumber + "': " + matches.size());
            } else if (request.getParameter("policyHolderId") != null) {
                Long id = Long.valueOf(request.getParameter("policyHolderId"));
                policyHolder = phService.getPolicyHolder(id);
            }
            if (policyHolder != null) {
                request.setAttribute("policyHolder", policyHolder);
                // TODO INC-4290: loads every line of every claim just to show a count
                List<ClaimDTO> claims = claimService.getClaimsForPolicyHolder(policyHolder.getPolicyHolderId());
                request.setAttribute("claims", claims);
            }
        } catch (ClaimServiceException e) {
            log.error("Policy holder lookup failed", e);
            ActionMessages errors = new ActionMessages();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.claim.service", e.getMessage()));
            saveErrors(request, errors);
        }
        return mapping.findForward("view");
    }
}
