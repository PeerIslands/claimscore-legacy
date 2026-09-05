<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!-- $Id: view.jsp,v 1.19 2009/02/24 16:05:31 mferrante Exp $ -->
<html>
<head>
<title><bean:message key="title.claim.view"/> <bean:write name="claim" property="claimNumber"/></title>
<style type="text/css">
  body { font-family: Verdana, Arial, sans-serif; font-size: 11px; background-color: #e8eef4; }
  .hdr { background-color: #2f5a8a; color: #ffffff; font-weight: bold; font-size: 12px; }
  .grid { border: 1px solid #6a8db3; background-color: #ffffff; }
  .grid th { background-color: #c9d8e8; text-align: left; font-size: 11px; }
  .even { background-color: #ffffff; } .odd { background-color: #eef3f8; }
  .lbl { font-weight: bold; width: 140px; } .amt { text-align: right; }
  .res { border: 1px solid #99aa66; background-color: #f4f8e8; }
</style>
</head>
<body>
<table width="100%" border="0" cellpadding="4" cellspacing="0">
  <tr><td class="hdr"><bean:message key="title.app"/> - <bean:message key="title.claim.view"/></td>
      <td class="hdr" align="right"><a style="color: #ffffff;" href="claimSearch.do"><bean:message key="link.search"/></a> | <a style="color: #ffffff;" href="claim.do"><bean:message key="link.submit"/></a> | <a style="color: #ffffff;" href="login.do?method=logout"><bean:message key="link.logout"/></a></td></tr>
</table>
<html:errors/>
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><td class="lbl"><bean:message key="label.claim.number"/></td><td><bean:write name="claim" property="claimNumber"/></td>
      <td class="lbl"><bean:message key="label.claim.status"/></td><td><bean:write name="claim" property="statusName"/> (<bean:write name="claim" property="statusCode"/>)</td></tr>
  <tr><td class="lbl"><bean:message key="label.member.number"/></td><td><a href="policyHolder.do?method=view&policyHolderId=<bean:write name="claim" property="policyHolderId"/>"><bean:write name="claim" property="memberNumber"/></a> - <bean:write name="claim" property="policyHolderName"/></td>
      <td class="lbl"><bean:message key="label.provider"/></td><td><bean:write name="claim" property="providerName"/></td></tr>
  <tr><td class="lbl"><bean:message key="label.incidentDate"/></td><td><bean:write name="claim" property="incidentDate" format="MM/dd/yyyy"/></td>
      <td class="lbl"><bean:message key="label.receivedDate"/></td><td><bean:write name="claim" property="receivedDate" format="MM/dd/yyyy"/></td></tr>
  <tr><td class="lbl"><bean:message key="label.claim.type"/></td><td><bean:write name="claim" property="claimTypeCode"/> / <bean:message key="label.placeOfService"/> <bean:write name="claim" property="placeOfServiceCode"/></td>
      <td class="lbl"><bean:message key="label.adjudicatedDate"/></td><td><bean:write name="claim" property="adjudicatedDate" format="MM/dd/yyyy"/></td></tr>
  <tr><td class="lbl"><bean:message key="label.totalBilled"/></td><td><bean:write name="claim" property="totalBilledAmount" format="$#,##0.00"/></td>
      <td class="lbl"><bean:message key="label.totalAllowed"/></td><td><bean:write name="claim" property="totalAllowedAmount" format="$#,##0.00"/></td></tr>
  <tr><td class="lbl"><bean:message key="label.totalPaid"/></td><td><bean:write name="claim" property="totalPaidAmount" format="$#,##0.00"/></td>
      <td class="lbl"><bean:message key="label.patientLiability"/></td><td><bean:write name="claim" property="patientLiabilityAmount" format="$#,##0.00"/></td></tr>
  <tr><td class="lbl"><bean:message key="label.deductibleApplied"/></td><td><bean:write name="claim" property="deductibleAppliedAmount" format="$#,##0.00"/></td>
      <td class="lbl"><bean:message key="label.denialReason"/></td><td><bean:write name="claim" property="denialReasonCode"/> &nbsp; v<bean:write name="claim" property="versionNo"/></td></tr>
</table>
<br/>
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><th><bean:message key="label.line.number"/></th><th><bean:message key="label.line.serviceCode"/></th><th><bean:message key="label.line.diagnosisCode"/></th><th><bean:message key="label.line.serviceDate"/></th>
      <th><bean:message key="label.line.units"/></th><th class="amt"><bean:message key="label.line.billed"/></th><th class="amt"><bean:message key="label.line.allowed"/></th><th class="amt"><bean:message key="label.line.paid"/></th><th><bean:message key="label.line.status"/></th></tr>
<% int rowNum = 0; %>
<logic:iterate id="line" name="claim" property="lines">
<% String rowClass = (rowNum++ % 2 == 0) ? "even" : "odd"; %>
  <tr class="<%= rowClass %>"><td><bean:write name="line" property="lineNumber"/></td><td><bean:write name="line" property="serviceCode"/></td><td><bean:write name="line" property="diagnosisCode"/></td>
      <td><bean:write name="line" property="serviceDate" format="MM/dd/yyyy"/></td><td><bean:write name="line" property="units"/></td>
      <td class="amt"><bean:write name="line" property="billedAmount" format="$#,##0.00"/></td><td class="amt"><bean:write name="line" property="allowedAmount" format="$#,##0.00"/></td><td class="amt"><bean:write name="line" property="paidAmount" format="$#,##0.00"/></td>
      <td><bean:write name="line" property="lineStatusCode"/> <bean:write name="line" property="denialReasonCode"/></td></tr>
</logic:iterate>
</table>
<logic:present name="result">
<br/>
<table class="res" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><td class="lbl"><bean:message key="label.result.code"/></td><td><bean:write name="result" property="resultCode"/></td>
      <td class="lbl"><bean:message key="label.result.message"/></td><td><bean:write name="result" property="message"/></td></tr>
  <tr><td class="lbl"><bean:message key="label.totalPaid"/></td><td><bean:write name="result" property="totalPaidAmount" format="$#,##0.00"/></td>
      <td class="lbl"><bean:message key="label.patientLiability"/></td><td><bean:write name="result" property="patientLiabilityAmount" format="$#,##0.00"/></td></tr>
</table>
</logic:present>
<br/>
<table width="100%" border="0" cellpadding="3" cellspacing="0"><tr>
  <td><form action="claim.do" method="post"><input type="hidden" name="method" value="adjudicate"/><input type="hidden" name="claimId" value="<bean:write name="claim" property="claimId"/>"/>
      <input type="submit" value="<bean:message key="button.adjudicate"/>"/></form></td>
  <td><form action="claim.do" method="post"><input type="hidden" name="method" value="reopen"/><input type="hidden" name="claimId" value="<bean:write name="claim" property="claimId"/>"/>
      <bean:message key="label.reason"/>: <input type="text" name="reason" size="40" maxlength="400"/> <input type="submit" value="<bean:message key="button.reopen"/>"/></form></td>
</tr></table>
<logic:present name="auditTrail">
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><th colspan="6"><bean:message key="label.audit.title"/></th></tr>
  <tr><th><bean:message key="label.audit.action"/></th><th><bean:message key="label.audit.oldStatus"/></th><th><bean:message key="label.audit.newStatus"/></th><th><bean:message key="label.audit.changedBy"/></th><th><bean:message key="label.audit.changedDate"/></th><th><bean:message key="label.audit.detail"/></th></tr>
  <logic:iterate id="row" name="auditTrail">
  <tr><td><bean:write name="row" property="ACTION_CD"/></td><td><bean:write name="row" property="OLD_STATUS_CD"/></td><td><bean:write name="row" property="NEW_STATUS_CD"/></td>
      <td><bean:write name="row" property="CHANGED_BY"/></td><td><bean:write name="row" property="CHANGED_DT" format="MM/dd/yyyy HH:mm"/></td><td><bean:write name="row" property="DETAIL"/></td></tr>
  </logic:iterate>
</table>
</logic:present>
<!-- TODO: hide adjudicate button when ClaimStatus.isTerminal (CR-2013-088) -->
</body>
</html>
