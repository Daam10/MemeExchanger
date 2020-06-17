<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants, java.util.List"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><spring:message code = "FailedTitleMsg"/></title>
</head>
<% 
List<String> errorMessages = (List<String>) session.getAttribute(ConfigConstants.failedPageErrorMessagesAttrName);
String errorMessage = (String) session.getAttribute(ConfigConstants.failedPageErrorMessageAttrName);
%>
<body>
<% 
if((errorMessages == null ? 0 : errorMessages.size()) + (errorMessage == null ? 0 : 1) == 0) {
%>
<h2><spring:message code="NoErrors"/></h2>
<% 
} else if((errorMessages == null ? 0 : errorMessages.size()) + (errorMessage == null ? 0 : 1) == 1) {
%>
<h2><spring:message code="Error"/>:</h2><br/>
<h4><%= errorMessage == null ? errorMessages.get(0) : errorMessage%></h4>
<% 
} else {
%>
<h2><spring:message code="Errors"/>:</h2><br/>
<%
	for(String eM : errorMessages){
%>
<h4><%= eM%></h4>
<%
	}
	if(errorMessage != null){
		%>
<h4><%= errorMessage%></h4>
		<%
	}
}
%>
</body>
</html>