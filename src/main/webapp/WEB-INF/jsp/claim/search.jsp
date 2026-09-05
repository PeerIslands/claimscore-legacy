<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!-- $Id: search.jsp,v 1.11 2008/12/02 10:17:44 mferrante Exp $ -->
<bean:parameter id="pMemberNumber" name="memberNumber" value=""/>
<bean:parameter id="pStatusCode" name="statusCode" value=""/>
<bean:parameter id="pFromDate" name="fromDate" value=""/>
<bean:parameter id="pToDate" name="toDate" value=""/>
<html>
<head>
<title><bean:message key="title.claim.search"/></title>
<style type="text/css">
  body { font-family: Verdana, Arial, sans-serif; font-size: 11px; background-color: #e8eef4; }
  .hdr { background-color: #2f5a8a; color: #ffffff; font-weight: bold; font-size: 12px; }
  .grid { border: 1px solid #6a8db3; background-color: #ffffff; }
  .grid th { background-color: #c9d8e8; text-align: left; font-size: 11px; }
  .even { background-color: #ffffff; } .odd { background-color: #eef3f8; }
  .amt { text-align: right; } .tot { font-weight: bold; background-color: #dde6f0; }
</style>
</head>
<body>
<table width="100%" border="0" cellpadding="4" cellspacing="0">
  <tr><td class="hdr"><bean:message key="title.app"/> - <bean:message key="title.claim.search"/></td>
      <td class="hdr" align="right"><a style="color: #ffffff;" href="claim.do"><bean:message key="link.submit"/></a> | <a style="color: #ffffff;" href="login.do?method=logout"><bean:message key="link.logout"/></a></td></tr>
</table>
<html:errors/>
<form action="claimSearch.do" method="get">
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr>
    <td><bean:message key="label.member.number"/>: <input type="text" name="memberNumber" size="14" maxlength="20" value="<bean:write name="pMemberNumber"/>"/></td>
    <td><bean:message key="label.claim.status"/>:
      <select name="statusCode">
        <option value=""><bean:message key="label.status.any"/></option>
        <logic:present name="statusOptions">
        <logic:iterate id="opt" name="statusOptions">
        <option value="<bean:write name="opt" property="key"/>"><bean:write name="opt" property="value"/></option>
        </logic:iterate>
        </logic:present>
      </select></td>
    <td><bean:message key="label.fromDate"/>: <input type="text" name="fromDate" size="10" maxlength="10" value="<bean:write name="pFromDate"/>"/></td>
    <td><bean:message key="label.toDate"/>: <input type="text" name="toDate" size="10" maxlength="10" value="<bean:write name="pToDate"/>"/></td>
    <td><input type="submit" value="<bean:message key="button.search"/>"/></td>
  </tr>
</table>
</form>
<br/>
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><th><bean:message key="label.claim.number"/></th><th><bean:message key="label.member.number"/></th><th><bean:message key="label.member.name"/></th><th><bean:message key="label.provider"/></th>
      <th><bean:message key="label.incidentDate"/></th><th><bean:message key="label.claim.status"/></th><th class="amt"><bean:message key="label.totalBilled"/></th><th class="amt"><bean:message key="label.totalPaid"/></th></tr>
<% int rowNum = 0; %>
<logic:present name="claims">
<logic:iterate id="c" name="claims">
<% String rowClass = (rowNum++ % 2 == 0) ? "even" : "odd"; %>
  <tr class="<%= rowClass %>">
    <td><a href="claim.do?method=view&claimId=<bean:write name="c" property="claimId"/>"><bean:write name="c" property="claimNumber"/></a></td>
    <td><bean:write name="c" property="memberNumber"/></td><td><bean:write name="c" property="policyHolderName"/></td><td><bean:write name="c" property="providerName"/></td>
    <td><bean:write name="c" property="incidentDate" format="MM/dd/yyyy"/></td><td><bean:write name="c" property="statusName"/></td>
    <td class="amt"><bean:write name="c" property="totalBilledAmount" format="$#,##0.00"/></td><td class="amt"><bean:write name="c" property="totalPaidAmount" format="$#,##0.00"/></td>
  </tr>
</logic:iterate>
</logic:present>
<logic:empty name="claims">
  <tr><td colspan="8"><bean:message key="label.noResults"/></td></tr>
</logic:empty>
  <tr class="tot"><td colspan="6"><bean:message key="label.totals"/> (<%= rowNum %>)</td>
    <td class="amt"><logic:present name="totalBilled"><bean:write name="totalBilled" format="$#,##0.00"/></logic:present></td>
    <td class="amt"><logic:present name="totalPaid"><bean:write name="totalPaid" format="$#,##0.00"/></logic:present></td></tr>
</table>
<!-- TODO: paging; MAX_SEARCH_ROWS silently truncates at 200 (INC-4118) -->
</body>
</html>
