<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.WebConstants, ru.miron.Config.ConfigConstants"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><spring:message code="MemeExchanger"/></title>
</head>
<%
boolean isAdmin = (Boolean) request.getAttribute(ConfigConstants.isAdminAttrName);
%>
<body>
<a href="<%= WebConstants.servletPrefix +  WebConstants.logoutURL%>"><spring:message code="ToLogout"/></a><br/>
<form method="GET" action="<%= WebConstants.servletPrefix +  WebConstants.createPostURL%>">
	<input type="submit" value="<spring:message code="ToCreatePost"/>">
</form>
<form method="GET" action="<%= WebConstants.servletPrefix +  WebConstants.feedURL%>">
	<input type="submit" value="<spring:message code="Feed"/>">
</form>
<% 
if(isAdmin){
%>
<form action="<%= WebConstants.servletPrefix + WebConstants.adminToolsMenuURL%>" method="GET">
	<input type="submit" value="<spring:message code="AdminTools"/>"/>
</form>
<% 
}
%>
</body>
</html>