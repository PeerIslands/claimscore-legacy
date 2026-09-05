<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" isErrorPage="true" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!-- $Id: error.jsp,v 1.2 2008/09/17 09:41:55 dkowalczyk Exp $ -->
<html>
<head>
<title><bean:message key="title.error"/></title>
<style type="text/css">
  body { font-family: Verdana, Arial, sans-serif; font-size: 11px; background-color: #e8eef4; }
  .box { border: 1px solid #6a8db3; background-color: #ffffff; }
  .hdr { background-color: #2f5a8a; color: #ffffff; font-weight: bold; font-size: 12px; }
  .err { color: #cc0000; font-weight: bold; }
  .msg { font-family: "Courier New", monospace; font-size: 10px; color: #333333; }
</style>
</head>
<body>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr><td align="center" valign="top" style="padding-top: 60px;">
    <table class="box" width="560" border="0" cellpadding="8" cellspacing="0">
      <tr><td class="hdr"><bean:message key="title.app"/> - <bean:message key="title.error"/></td></tr>
      <tr><td class="err"><html:errors/></td></tr>
      <tr><td class="msg">
<%
    String detail = null;
    if (exception != null) {
        detail = exception.getMessage();
    } else if (request.getAttribute("javax.servlet.error.message") != null) {
        detail = String.valueOf(request.getAttribute("javax.servlet.error.message"));
    }
    if (detail != null) {
%>
        <bean:message key="label.error.detail"/>: <%= detail %>
<%
    }
%>
      </td></tr>
      <tr><td>
        <a href="claimSearch.do"><bean:message key="link.search"/></a> &nbsp;|&nbsp;
        <a href="claim.do"><bean:message key="link.submit"/></a> &nbsp;|&nbsp;
        <a href="login.do?method=logout"><bean:message key="link.logout"/></a>
      </td></tr>
    </table>
  </td></tr>
</table>
</body>
</html>
