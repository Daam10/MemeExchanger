<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants, ru.miron.Entities.Ban, java.util.*, ru.miron.Config.WebConstants, java.util.Date, ru.miron.Utils.CommonUtils, ru.miron.Enums.TypesOfEntity"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

<% 
List<Ban> bans = (List<Ban>) request.getAttribute(ConfigConstants.getBansPageBansAttrName);
String whenSwitchedURL = (String) request.getAttribute(ConfigConstants.getBansPageWhenSwitchedURLAttrName);
TypesOfEntity typeOfEntity = (TypesOfEntity) request.getAttribute(ConfigConstants.entitiesBanManagementPageTypeOfEntityParam);
boolean isFirstSet = (Boolean) request.getAttribute(ConfigConstants.isFirstSetAttrName);
%>

<% 
if(bans == null || bans.isEmpty()){
%>
<spring:message code="NoBans"/>
<%
} else {
	for(Ban ban : bans){
%>
<form action = "<%= WebConstants.servletPrefix + whenSwitchedURL%>" id="<%= "changeBanStateForm" + ban.getId()%>" method = "post">
	<spring:message code="FromForExampleDateMsg"/>: <input type="date" name="<%= ConfigConstants.getBansPageFromDateParamName%>" value="<%= ban.getFrom()%>" readonly/>
	<spring:message code="ToForExampleDateMsg"/>: <input type="date" name="<%= ConfigConstants.getBansPageToDateParamName%>" value="<%= ban.getTo()%>" readonly/>
	<input type="submit" form="<%= "changeBanStateForm" + ban.getId()%>" 
		value=
		<% 
		if(ban.getRemoved()){ 
		%> 
		"<spring:message code="doActive"/>" name="<%= ConfigConstants.getBansPageDoActiveParamName%>" readonly/> 
		<% 
		} else {
		%> 
		"<spring:message code="doUnactive"/>" name="<%= ConfigConstants.getBansPageDoUnactiveParamName%>" readonly/> 
		<%}
		if(isFirstSet){%>
		<input type="hidden" id="<%= ConfigConstants.bansFirstIDFromFirstSetParamName%>" name="<%= ConfigConstants.identificatorParamName%>" value="<%= ban.getId()%>"/>
		<%
		} else {
		%>
		<input type="hidden" name="<%= ConfigConstants.identificatorParamName%>" value="<%= ban.getId()%>"/>
		<% 
		}
		%>
	<input type="hidden" name="<%= ConfigConstants.entitiesBanManagementPageTypeOfEntityParam%>" value="<%= typeOfEntity.getTypeName()%>"/>
</form>
<% 
	}
	%>
	<script>
		countTo += <%= bans.size()%>;
		countFrom += <%= bans.size()%>;
	</script>
	<%
}
%>
