package com.claimscore.struts;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.claimscore.dao.LookupDAO;
import com.claimscore.dto.ClaimDTO;
import com.claimscore.ejb.ClaimService;
import com.claimscore.ejb.ClaimServiceException;
import com.claimscore.util.Constants;
import com.claimscore.util.DateUtil;

/**
 * Claim search screen: criteria in, result rows and grand totals out.
 *
 * @author mferrante
 * $Id: ClaimSearchAction.java,v 1.14 2011/08/30 16:02:44 rmendez Exp $
 */
public class ClaimSearchAction extends Action {

    private static final Logger log = Logger.getLogger(ClaimSearchAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (request.getSession().getAttribute(Constants.SESSION_USER_KEY) == null) {
            return mapping.findForward("login");
        }
        String memberNumber = StringUtils.trimToNull(request.getParameter("memberNumber"));
        String statusCode = StringUtils.trimToNull(request.getParameter("statusCode"));
        Date fromDate = parseDate(request.getParameter("fromDate"));
        Date toDate = parseDate(request.getParameter("toDate"));

        List claims = new ArrayList();
        double totalBilled = 0;
        double totalPaid = 0;
        try {
            ClaimService claimService = (ClaimService) new InitialContext().lookup(Constants.JNDI_CLAIM_SERVICE);
            List<ClaimDTO> found = claimService.searchClaims(memberNumber, statusCode, fromDate, toDate);
            for (ClaimDTO dto : found) {
                if (dto.getTotalBilledAmount() != null) {
                    totalBilled += dto.getTotalBilledAmount().doubleValue();
                }
                if (dto.getTotalPaidAmount() != null) {
                    totalPaid += dto.getTotalPaidAmount().doubleValue();
                }
                claims.add(dto);
            }
        } catch (ClaimServiceException e) {
            log.error("Claim search failed", e);
            ActionMessages errors = new ActionMessages();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.claim.service", e.getMessage()));
            saveErrors(request, errors);
        }
        if (log.isDebugEnabled()) {
            log.debug("Search returned " + claims.size() + " claims, billed=" + totalBilled + " paid=" + totalPaid);
        }
        request.setAttribute("claims", claims);
        request.setAttribute("totalBilled", new Double(totalBilled));
        request.setAttribute("totalPaid", new Double(totalPaid));
        request.setAttribute("statusOptions",
                new LookupDAO().loadLookup("LK_CLAIM_STATUS", "STATUS_CD", "STATUS_NAME"));
        return mapping.findForward("search");
    }

    private static Date parseDate(String s) {
        try {
            return DateUtil.parse(s);
        } catch (ParseException e) {
            log.debug("Ignoring unparseable date '" + s + "'");
            return null;
        }
    }
}
