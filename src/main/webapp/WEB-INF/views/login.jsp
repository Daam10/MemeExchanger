<%@ page language="java" 
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants, ru.miron.Config.WebConstants"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><spring:message code="Enter"/></title>
</head>
<body>
<%
String wrongLoginOrPasswordMsg = (String) request.getAttribute(ConfigConstants.loginPageWrongLoginOrPasswordMsgAttrName);
%>
<a href="<%= WebConstants.servletPrefix +  WebConstants.feedURL%>"><spring:message code="ToMenu"/></a><br/>
<form action="login" method="POST">
	<spring:message code="Login"/>: <input name="<%= ConfigConstants.loginPageLoginParamName%>"/><br/>
	<spring:message code="Password"/>: <input type="password" name="<%= ConfigConstants.loginPagePasswordParamName%>"/><br/>
	<%
	if(wrongLoginOrPasswordMsg != null) {%>
	<h5 style="color: red"><%= wrongLoginOrPasswordMsg %></h5><br/>
	<%
	}
	%>
	<input type="submit" value="<spring:message code="ToLogin"/>">
</form>
</body>
</html>