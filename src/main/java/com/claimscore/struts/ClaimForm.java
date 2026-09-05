package com.claimscore.struts;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.claimscore.dto.ClaimDTO;
import com.claimscore.dto.ClaimLineDTO;
import com.claimscore.util.DateUtil;
import com.claimscore.util.MoneyUtil;

/**
 * Struts form bean for the claim submit screen (three fixed line rows).
 *
 * @author dkowalczyk
 * $Id: ClaimForm.java,v 1.12 2009/01/15 10:42:18 dkowalczyk Exp $
 */
public class ClaimForm extends ActionForm {

    private static final long serialVersionUID = 1L;

    private String claimId;
    private String claimNumber;
    private String memberNumber;
    private String providerId;
    private String claimTypeCode;
    private String placeOfServiceCode;
    private String incidentDate;
    private String method;
    private String[] lineServiceCodes = new String[0];
    private String[] lineDiagnosisCodes = new String[0];
    private String[] lineServiceDates = new String[0];
    private String[] lineUnits = new String[0];
    private String[] lineBilledAmounts = new String[0];

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (StringUtils.isBlank(memberNumber)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.claim.member.required"));
        }
        try {
            Long.parseLong(providerId == null ? "" : providerId.trim());
        } catch (NumberFormatException e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.claim.provider.invalid"));
        }
        try {
            Date incident = DateUtil.parse(incidentDate);
            if (incident == null || incident.after(new Date())) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.claim.incidentDate.invalid"));
            }
        } catch (ParseException e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.claim.incidentDate.invalid"));
        }
        int lineCount = 0;
        boolean badAmount = false;
        for (int i = 0; i < lineServiceCodes.length; i++) {
            if (StringUtils.isBlank(lineServiceCodes[i])) {
                continue;
            }
            lineCount++;
            String amt = i < lineBilledAmounts.length ? lineBilledAmounts[i] : null;
            try {
                if (MoneyUtil.parse(amt).compareTo(BigDecimal.ZERO) <= 0) {
                    badAmount = true;
                }
            } catch (NumberFormatException e) {
                badAmount = true;
            }
        }
        if (lineCount == 0) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.claim.lines.required"));
        }
        if (badAmount) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.claim.line.amount"));
        }
        return errors;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        lineServiceCodes = new String[0];
        lineDiagnosisCodes = new String[0];
        lineServiceDates = new String[0];
        lineUnits = new String[0];
        lineBilledAmounts = new String[0];
    }

    // FIXME CR-2013-088: the JSP always posts 3 rows, blank rows are skipped by service code only
    public ClaimDTO toClaimDTO() throws ParseException {
        ClaimDTO dto = new ClaimDTO();
        dto.setMemberNumber(memberNumber.trim());
        dto.setProviderId(Long.valueOf(providerId.trim()));
        dto.setClaimTypeCode(claimTypeCode);
        dto.setPlaceOfServiceCode(placeOfServiceCode);
        Date incident = DateUtil.parse(incidentDate);
        dto.setIncidentDate(incident);
        int lineNo = 0;
        for (int i = 0; i < lineServiceCodes.length; i++) {
            if (StringUtils.isBlank(lineServiceCodes[i])) {
                continue;
            }
            ClaimLineDTO line = new ClaimLineDTO();
            line.setLineNumber(++lineNo);
            line.setServiceCode(lineServiceCodes[i].trim().toUpperCase());
            line.setDiagnosisCode(i < lineDiagnosisCodes.length && !StringUtils.isBlank(lineDiagnosisCodes[i])
                    ? lineDiagnosisCodes[i].trim().toUpperCase() : null);
            Date svcDate = i < lineServiceDates.length ? DateUtil.parse(lineServiceDates[i]) : null;
            line.setServiceDate(svcDate == null ? incident : svcDate);
            line.setUnits(i < lineUnits.length && !StringUtils.isBlank(lineUnits[i]) ? Integer.parseInt(lineUnits[i].trim()) : 1);
            line.setBilledAmount(MoneyUtil.parse(i < lineBilledAmounts.length ? lineBilledAmounts[i] : null));
            line.setLineStatusCode("PND");
            dto.addLine(line);
        }
        return dto;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getClaimTypeCode() {
        return claimTypeCode;
    }

    public void setClaimTypeCode(String claimTypeCode) {
        this.claimTypeCode = claimTypeCode;
    }

    public String getPlaceOfServiceCode() {
        return placeOfServiceCode;
    }

    public void setPlaceOfServiceCode(String placeOfServiceCode) {
        this.placeOfServiceCode = placeOfServiceCode;
    }

    public String getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(String incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String[] getLineServiceCodes() {
        return lineServiceCodes;
    }

    public void setLineServiceCodes(String[] lineServiceCodes) {
        this.lineServiceCodes = lineServiceCodes == null ? new String[0] : lineServiceCodes;
    }

    public String[] getLineDiagnosisCodes() {
        return lineDiagnosisCodes;
    }

    public void setLineDiagnosisCodes(String[] lineDiagnosisCodes) {
        this.lineDiagnosisCodes = lineDiagnosisCodes == null ? new String[0] : lineDiagnosisCodes;
    }

    public String[] getLineServiceDates() {
        return lineServiceDates;
    }

    public void setLineServiceDates(String[] lineServiceDates) {
        this.lineServiceDates = lineServiceDates == null ? new String[0] : lineServiceDates;
    }

    public String[] getLineUnits() {
        return lineUnits;
    }

    public void setLineUnits(String[] lineUnits) {
        this.lineUnits = lineUnits == null ? new String[0] : lineUnits;
    }

    public String[] getLineBilledAmounts() {
        return lineBilledAmounts;
    }

    public void setLineBilledAmounts(String[] lineBilledAmounts) {
        this.lineBilledAmounts = lineBilledAmounts == null ? new String[0] : lineBilledAmounts;
    }
}
