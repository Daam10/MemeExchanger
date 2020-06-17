<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants, ru.miron.Config.WebConstants, java.util.List"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><spring:message code="PostCreating"/></title>
</head>
<body>
<%
List<String> fileBeforeInsertErrors = (List<String>) session.getAttribute(ConfigConstants.createPostPageFileBeforeInsertErrorsAttrName);
List<String> fileAfterInsertErrors = (List<String>) session.getAttribute(ConfigConstants.createPostPageFileAfterInsertErrorsAttrName);
List<String> titleErrors = (List<String>) session.getAttribute(ConfigConstants.createPostPageTitleErrorsAttrName);
String tooManyPostsPerDayMsg = (String) session.getAttribute(ConfigConstants.createPostPageTooManyPostsPerDayMsgAttrName);
session.setAttribute(ConfigConstants.createPostPageFileBeforeInsertErrorsAttrName, null);
session.setAttribute(ConfigConstants.createPostPageFileAfterInsertErrorsAttrName, null);
session.setAttribute(ConfigConstants.createPostPageTitleErrorsAttrName, null);
session.setAttribute(ConfigConstants.createPostPageTooManyPostsPerDayMsgAttrName, null);
%>
<a href="<%= WebConstants.servletPrefix +  WebConstants.mainURL%>"><spring:message code="ToMenu"/></a><br/>
<form method="POST" action="<%= WebConstants.servletPrefix +  WebConstants.createPostURL%>" enctype="multipart/form-data">
	<spring:message code="PostTitle"/><input name="<%= ConfigConstants.createPostPageTitleParamName%>"/><br/>
	<% 
	if(titleErrors != null){ 
		for(String error : titleErrors){
	%>
	<h5 style="color: red"><%= error %></h5><br/>
	<%
		}
	}
	%>
	<input type="file" name="<%= ConfigConstants.createPostPageFileParamName%>"/><br/>
	<% 
	if(fileBeforeInsertErrors != null){ 
		for(String error : fileBeforeInsertErrors){
	%>
	<h5 style="color: red"><%= error %></h5><br/>
	<%
		}
	}
	if(fileAfterInsertErrors != null){ 
		for(String error : fileAfterInsertErrors){
	%>
	<h5 style="color: red"><%= error %></h5><br/>
	<%
		}
	}
	%>
	<input type="submit" value="<spring:message code="ToPost"/>">
	<%
	if(tooManyPostsPerDayMsg != null){ 
	%>
	<h5 style="color: red"><%= tooManyPostsPerDayMsg %></h5><br/>
	<%
	}
	%>
</form>
</body>
</html>