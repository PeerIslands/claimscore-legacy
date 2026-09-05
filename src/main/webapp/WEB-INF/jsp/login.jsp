<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!-- $Id: login.jsp,v 1.4 2008/11/03 14:22:10 dkowalczyk Exp $ -->
<html>
<head>
<title><bean:message key="title.login"/></title>
<style type="text/css">
  body { font-family: Verdana, Arial, sans-serif; font-size: 11px; background-color: #e8eef4; }
  .box { border: 1px solid #6a8db3; background-color: #ffffff; }
  .hdr { background-color: #2f5a8a; color: #ffffff; font-weight: bold; font-size: 12px; }
  .err { color: #cc0000; font-weight: bold; }
</style>
</head>
<body>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr><td align="center" valign="middle" height="400">
    <table class="box" width="380" border="0" cellpadding="6" cellspacing="0">
      <tr><td class="hdr" colspan="2"><bean:message key="title.app"/> - <bean:message key="title.login"/></td></tr>
      <tr><td colspan="2" class="err"><html:errors/></td></tr>
      <html:form action="/login" method="post" focus="username">
      <tr>
        <td width="35%"><bean:message key="label.username"/>:</td>
        <td><html:text property="username" size="20" maxlength="30"/></td>
      </tr>
      <tr>
        <td><bean:message key="label.password"/>:</td>
        <td><html:password property="password" size="20" maxlength="40" redisplay="false"/></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td><html:submit><bean:message key="button.login"/></html:submit></td>
      </tr>
      </html:form>
      <tr><td colspan="2" style="font-size: 9px; color: #666666;">
        ClaimsCore 2.7 &nbsp;|&nbsp; Larkspur Mutual Health Plan &nbsp;|&nbsp; Authorized personnel only.
        <!-- FIXME: lockout after FAILED_ATTEMPTS >= 5 never implemented (INC-3902) -->
      </td></tr>
    </table>
  </td></tr>
</table>
</body>
</html>
