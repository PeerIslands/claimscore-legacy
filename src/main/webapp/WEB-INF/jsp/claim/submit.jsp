<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!-- $Id: submit.jsp,v 1.9 2008/11/20 08:55:02 dkowalczyk Exp $ -->
<html>
<head>
<title><bean:message key="title.claim.submit"/></title>
<style type="text/css">
  body { font-family: Verdana, Arial, sans-serif; font-size: 11px; background-color: #e8eef4; }
  .hdr { background-color: #2f5a8a; color: #ffffff; font-weight: bold; font-size: 12px; }
  .grid { border: 1px solid #6a8db3; background-color: #ffffff; }
  .grid th { background-color: #c9d8e8; text-align: left; font-size: 11px; }
  .lbl { font-weight: bold; width: 160px; } .err { color: #cc0000; font-weight: bold; }
</style>
</head>
<body>
<table width="100%" border="0" cellpadding="4" cellspacing="0">
  <tr><td class="hdr"><bean:message key="title.app"/> - <bean:message key="title.claim.submit"/></td>
      <td class="hdr" align="right"><a style="color: #ffffff;" href="claimSearch.do"><bean:message key="link.search"/></a> | <a style="color: #ffffff;" href="login.do?method=logout"><bean:message key="link.logout"/></a></td></tr>
</table>
<div class="err"><html:errors/></div>
<html:form action="/claim" method="post" focus="memberNumber">
<html:hidden property="method" value="submit"/>
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><td class="lbl"><bean:message key="label.member.number"/></td><td><html:text property="memberNumber" size="14" maxlength="20"/></td>
      <td class="lbl"><bean:message key="label.provider"/> (ID)</td><td><html:text property="providerId" size="10" maxlength="12"/></td></tr>
  <tr><td class="lbl"><bean:message key="label.claim.type"/></td>
      <td><html:select property="claimTypeCode">
            <html:option value="PROF">Professional</html:option><html:option value="INST">Institutional</html:option>
            <html:option value="PHRM">Pharmacy</html:option><html:option value="DENT">Dental</html:option>
          </html:select></td>
      <td class="lbl"><bean:message key="label.placeOfService"/></td>
      <td><html:select property="placeOfServiceCode">
            <html:option value="11">11 - Office</html:option><html:option value="21">21 - Inpatient Hospital</html:option>
            <html:option value="22">22 - Outpatient Hospital</html:option><html:option value="23">23 - Emergency Room</html:option>
            <html:option value="81">81 - Independent Lab</html:option>
          </html:select></td></tr>
  <tr><td class="lbl"><bean:message key="label.incidentDate"/></td><td colspan="3"><html:text property="incidentDate" size="10" maxlength="10"/> (MM/DD/YYYY)</td></tr>
</table>
<br/>
<table class="grid" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr><th><bean:message key="label.line.number"/></th><th><bean:message key="label.line.serviceCode"/></th><th><bean:message key="label.line.diagnosisCode"/></th>
      <th><bean:message key="label.line.serviceDate"/></th><th><bean:message key="label.line.units"/></th><th><bean:message key="label.line.billed"/></th></tr>
<% for (int i = 1; i <= 3; i++) { %>
  <tr style="background-color: <%= (i % 2 == 0) ? "#eef3f8" : "#ffffff" %>;">
    <td><%= i %></td>
    <td><input type="text" name="lineServiceCodes" size="10" maxlength="10"/></td>
    <td><input type="text" name="lineDiagnosisCodes" size="10" maxlength="10"/></td>
    <td><input type="text" name="lineServiceDates" size="10" maxlength="10"/></td>
    <td><input type="text" name="lineUnits" size="4" maxlength="5" value="1"/></td>
    <td><input type="text" name="lineBilledAmounts" size="12" maxlength="14"/></td>
  </tr>
<% } %>
  <tr><td colspan="6" style="font-size: 9px; color: #666666;">
    Service codes SVC-nnnnn, diagnosis codes DX-Annn. Leave a row blank to skip it.
    <!-- FIXME: line values are not redisplayed after a validation error (CR-2013-088) -->
  </td></tr>
</table>
<br/>
<html:submit><bean:message key="button.submit"/></html:submit>
<html:reset><bean:message key="button.reset"/></html:reset>
</html:form>
</body>
</html>
