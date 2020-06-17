<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants, ru.miron.Config.WebConstants, java.util.Date, ru.miron.Utils.WebUtils"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><spring:message code="AdminTools"/></title>
</head>
<% 
String illegalEntityTypeMsg = (String) session.getAttribute(ConfigConstants.adminToolsMenuPageIllegalEntityTypeMsgAttrName);
String illegalIdentificatorTypeMsg = (String) session.getAttribute(ConfigConstants.adminToolsMenuPageIllegalIdentificatorTypeMsgAttrName);
session.setAttribute(ConfigConstants.adminToolsMenuPageIllegalEntityTypeMsgAttrName, null);
session.setAttribute(ConfigConstants.adminToolsMenuPageIllegalIdentificatorTypeMsgAttrName, null);
%>
<body>
<a href="<%= WebConstants.servletPrefix +  WebConstants.mainURL%>"><spring:message code="ToMenu"/></a><br/>
<h3><spring:message code="BanManagement"/>:</h3><br/>
<form action="<%= WebConstants.servletPrefix + WebConstants.adminToolsMenuURL + "/" + WebConstants.adminToolsMenuUsersBanURL%>" method="GET">
	<input type="submit" value="<spring:message code="OfUsers"/>"/>
</form>
<form action="<%= WebConstants.servletPrefix + WebConstants.adminToolsMenuURL + "/" + WebConstants.adminToolsMenuPostsBanURL%>" method="GET">
	<input type="submit" value="<spring:message code="OfPosts"/>"/>
</form>
<form action="<%= WebConstants.servletPrefix + WebConstants.adminToolsMenuURL + "/" + WebConstants.adminToolsMenuIPAddressesBanURL%>" method="GET">
	<input type="submit" value="<spring:message code="OfIPAddresses"/>"/>
</form>
<form action="<%= WebConstants.servletPrefix + WebConstants.adminToolsMenuURL + "/" + WebConstants.adminToolsMenuPostCommentsBanURL%>" method="GET">
	<input type="submit" value="<spring:message code="OfPostComments"/>"/>
</form>
<%
if(illegalEntityTypeMsg != null){
%>
<h5 style="color: red"><%= illegalEntityTypeMsg %></h5><br/>
<% 
}
if(illegalIdentificatorTypeMsg != null){
%>
<h5 style="color: red"><%= illegalIdentificatorTypeMsg %></h5><br/>
<% 
}
%>
</body>
</html>