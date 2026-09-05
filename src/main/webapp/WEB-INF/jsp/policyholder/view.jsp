<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!-- $Id: view.jsp,v 1.7 2008/12/15 13:02:19 mferrante Exp $ -->
<html>
<head>
<title><bean:message key="title.policyholder.view"/></title>
<style type="text/css">
  body { font-family: Verdana, Arial, sans-serif; font-size: 11px; background-color: #e8eef4; }
  .hdr { background-color: #2f5a8a; color: #ffffff; font-weight: bold; font-size: 12px; }
  .grid { border: 1px solid #6a8db3; background-color: #ffffff; }
  .grid th { background-color: #c9d8e8; text-align: left; font-size: 11px; }
  .even { background-color: #ffffff; } .odd { background-color: #eef3f8; }
  .lbl { font-weight: bold; width: 140px; } .amt { text-align: right; }
</style>
</head>
<body>
<table width="100%" border="0" cellpadding="4" cellspacing="0">
  <tr><td class="hdr"><bean:message key="title.app"/> - <bean:message key="title.policyholder.view"/></td>
      <td class="hdr" align="right"><a style="color: #ffffff;" href="claimSearch.do"><bean:message key="link.search"/></a> | <a style="color: #ffffff;" href="claim.do"><bean:message key="link.submit"/></a> | <a style="color: #ffffff;" href="login.do?method=logout"><bean:message key="link.logout"/></a></td></tr>
</table>
<html:errors/>
<form action="policyHolder.do" method="get">
<input type="hidden" name="method" value="search"/>
<bean:message key="label.member.lastName"/>: <input type="text" name="lastName" size="20" maxlength="60"/> &nbsp;
<bean:message key="label.member.number"/>: <input type="text" name="memberNumber" size="14" maxlength="20"/> &nbsp;
<input type="submit" value="<bean:message key="button.search"/>"/>
</form>
<br/>
<logic:present name="policyHolder">
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><td class="lbl"><bean:message key="label.member.number"/></td><td><bean:write name="policyHolder" property="memberNumber"/></td>
      <td class="lbl"><bean:message key="label.member.name"/></td><td><bean:write name="policyHolder" property="fullName"/></td></tr>
  <tr><td class="lbl"><bean:message key="label.member.birthDate"/></td><td><bean:write name="policyHolder" property="birthDate" format="MM/dd/yyyy"/> (<bean:write name="policyHolder" property="genderCode"/>)</td>
      <td class="lbl"><bean:message key="label.member.plan"/></td><td><bean:write name="policyHolder" property="activePlanCode"/> &nbsp; <bean:message key="label.claim.status"/>: <bean:write name="policyHolder" property="statusCode"/></td></tr>
  <tr><td class="lbl">SSN</td><td colspan="3">***-**-<bean:write name="policyHolder" property="ssnLast4"/></td></tr>
</table>
<br/>
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><th><bean:message key="label.address.type"/></th><th><bean:message key="label.address"/></th><th><bean:message key="label.address.city"/></th><th><bean:message key="label.address.state"/></th><th><bean:message key="label.address.zip"/></th><th><bean:message key="label.address.effective"/></th></tr>
  <logic:iterate id="addr" name="policyHolder" property="addresses">
  <tr><td><bean:write name="addr" property="addressTypeCode"/></td><td><bean:write name="addr" property="line1"/> <bean:write name="addr" property="line2"/></td>
      <td><bean:write name="addr" property="city"/></td><td><bean:write name="addr" property="stateCode"/></td><td><bean:write name="addr" property="zip"/></td>
      <td><bean:write name="addr" property="effectiveDate" format="MM/dd/yyyy"/> - <bean:write name="addr" property="endDate" format="MM/dd/yyyy"/></td></tr>
  </logic:iterate>
</table>
<br/>
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><th><bean:message key="label.claim.number"/></th><th><bean:message key="label.provider"/></th><th><bean:message key="label.incidentDate"/></th><th><bean:message key="label.claim.status"/></th>
      <th><bean:message key="label.lineCount"/></th><th class="amt"><bean:message key="label.totalBilled"/></th><th class="amt"><bean:message key="label.totalPaid"/></th></tr>
<% int rowNum = 0; %>
<logic:present name="claims">
<logic:iterate id="c" name="claims">
<% String rowClass = (rowNum++ % 2 == 0) ? "even" : "odd"; %>
  <tr class="<%= rowClass %>"><td><a href="claim.do?method=view&claimId=<bean:write name="c" property="claimId"/>"><bean:write name="c" property="claimNumber"/></a></td>
      <td><bean:write name="c" property="providerName"/></td><td><bean:write name="c" property="incidentDate" format="MM/dd/yyyy"/></td><td><bean:write name="c" property="statusName"/></td>
      <td><bean:write name="c" property="lineCount"/></td><td class="amt"><bean:write name="c" property="totalBilledAmount" format="$#,##0.00"/></td><td class="amt"><bean:write name="c" property="totalPaidAmount" format="$#,##0.00"/></td></tr>
</logic:iterate>
</logic:present>
<logic:empty name="claims"><tr><td colspan="7"><bean:message key="label.noResults"/></td></tr></logic:empty>
</table>
</logic:present>
<logic:notPresent name="policyHolder"><bean:message key="label.noResults"/></logic:notPresent>
<!-- TODO: claims list loads every line for every claim; slow for members with 100+ claims (INC-4290) -->
</body>
</html>
