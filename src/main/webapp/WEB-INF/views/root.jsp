<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.WebConstants"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><spring:message code="MemeExchanger"/></title>
</head>
<body>
<form method="GET" action="<%= WebConstants.servletPrefix + WebConstants.loginURL%>">
	<input type="submit" value="<spring:message code="ToLogin"/>">
</form>
<form method="GET" action="<%= WebConstants.servletPrefix + WebConstants.registrationURL%>">
	<input type="submit" value="<spring:message code="ToRegister"/>">
</form>
</body>
</html>