<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants, ru.miron.Entities.Ban, java.util.*, ru.miron.Config.WebConstants, java.util.Date, ru.miron.Utils.CommonUtils, ru.miron.Entities.IPAddress"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<%
IPAddress ipAddress = (IPAddress) request.getAttribute(ConfigConstants.getIPAddressPageIPAddressAttrName);
%>
<body>
	<%
	if(ipAddress == null){
	%>
	<h5 style="color: red"><spring:message code="LoginOrIPAddressDoesntExist"/></h5>
	<%
	} else {
	%>
	<spring:message code="Id"/>: <%= ipAddress.getId()%><br/>
	<spring:message code="IPAddress"/>: <%= ipAddress.getName()%>
	<% 
	}
	%>
</body>
</html>