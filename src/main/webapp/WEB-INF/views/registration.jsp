<%@page import="ru.miron.Config.WebConstants"%>
<%@ page language="java" 
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants,java.util.List"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><spring:message code="Registration"/></title>
</head>
<body>
<%
String loginAlreadyExistsMsg = (String) request.getAttribute(ConfigConstants.registrationPageLoginAlreadyExistsMsgAttrName);
List<String> loginErrors = (List<String>) request.getAttribute(ConfigConstants.registrationPageLoginErrorsAttrName);
List<String> passwordErrors = (List<String>) request.getAttribute(ConfigConstants.registrationPagePasswordErrorsAttrName);
String tooManyAccountsMsg = (String) request.getAttribute(ConfigConstants.registrationPageTooManyAccountsMsgAttrName);
%>
<a href="<%= WebConstants.servletPrefix +  WebConstants.feedURL%>"><spring:message code="ToMenu"/></a><br/>
<form action="<%= WebConstants.servletPrefix + WebConstants.registrationURL %>" method="POST">
	<spring:message code="Login"/>: <input name="<%= ConfigConstants.registrationPageLoginParamName%>"/><br/>
	<% 
	if(loginAlreadyExistsMsg != null) { 
	%>
	<h5 style="color: red"><%= loginAlreadyExistsMsg %></h5><br/>
	<% 
	}
	if(loginErrors != null) { 
		for(String error : loginErrors){
	%>
	<h5 style="color: red"><%= error %></h5><br/>
	<% 
		}
	}
	%>
	<spring:message code="Password"/>: <input type="password" name="<%= ConfigConstants.registrationPagePasswordParamName%>"/><br/>
	<%  
	if(passwordErrors != null) { 
		for(String error : passwordErrors){
	%>
	<h5 style="color: red"><%= error %></h5><br/>
	<% 
		}
	}
	%>
	<input type="submit" value="<spring:message code="ToRegister"/>">
	<% 
	if(tooManyAccountsMsg != null) { 
	%>
	<h5 style="color: red"><%= tooManyAccountsMsg %></h5><br/>
	<% 
	}
	%>
</form>
</body>
</html>