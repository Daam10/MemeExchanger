<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants, ru.miron.Config.WebConstants, java.util.Date, ru.miron.Enums.*"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<% 
String title = (String) request.getAttribute(ConfigConstants.entitiesBanManagementPageTitleAttrName);
%>
<title><%= title%></title>
<% 

String entityDoesntExistMsg = (String) session.getAttribute(ConfigConstants.entitiesBanManagementPageEntityDoesntExistMsgAttrName);
String toDateErrorMsg = (String) session.getAttribute(ConfigConstants.entitiesBanManagementPageWrongToDateErrorMsgAttrName);
String mayBeDidntSetDateMsg = (String) session.getAttribute(ConfigConstants.entitiesBanManagementPageMaybeDidntSetDateMsgAttrName);
String fromOrToDateParseExceptionMsg = (String) session.getAttribute(ConfigConstants.entitiesBanManagementPageFromOrToDateParseExceptionMsgAttrName);
String illegalIdentificatorMsg = (String) session.getAttribute(ConfigConstants.entitiesBanManagementPageIllegalIdentificatorMsgAttrName);

String identificatorName = (String) request.getAttribute(ConfigConstants.entitiesBanManagementPageIdentificatorNameAttrName);
TypesOfEntity typeOfEntity = (TypesOfEntity) request.getAttribute(ConfigConstants.entitiesBanManagementPageTypeOfEntityParam);
TypesOfIdentificator typeOfIdentificator = (TypesOfIdentificator) request.getAttribute(ConfigConstants.typeOfIdentificatorParamName);

session.setAttribute(ConfigConstants.entitiesBanManagementPageEntityDoesntExistMsgAttrName, null);
session.setAttribute(ConfigConstants.entitiesBanManagementPageWrongToDateErrorMsgAttrName, null);
session.setAttribute(ConfigConstants.entitiesBanManagementPageMaybeDidntSetDateMsgAttrName, null);
session.setAttribute(ConfigConstants.entitiesBanManagementPageFromOrToDateParseExceptionMsgAttrName, null);
session.setAttribute(ConfigConstants.entitiesBanManagementPageIllegalIdentificatorMsgAttrName, null);
%>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script type="text/javascript">
function loadIPAddress(){
	$.ajax({
		type: "POST",
		url: "<%= WebConstants.servletPrefix + WebConstants.adminToolsMenuURL + "/" + WebConstants.getIPAddressByUserURL%>", 
		data: {<%= ConfigConstants.typeOfIdentificatorParamName%>: "<%= TypesOfIdentificator.LOGIN.getTypeName()%>",
			<%= ConfigConstants.identificatorParamName%>: $('#<%= TypesOfIdentificator.LOGIN.getTypeName()%>').val()},
		success: 
			function(data) { // can contain errors instead of user bans
				//prompt("Copy to clipboard: Ctrl+C, Enter", data);
				$('#ipContainer').html(data);
	  		}
		})
}
var countToGet = 5;
var countFrom = 0;
var countTo = countToGet;
function loadBans(){
	$.ajax({
		type: "GET",
		url: "<%= WebConstants.servletPrefix + WebConstants.adminToolsMenuURL + "/" + WebConstants.getBansURL%>", 
		data: {<%= ConfigConstants.countFromParamName%>: countFrom.valueOf(),
			<%= ConfigConstants.countToParamName%>: countTo.valueOf(),
			<%= ConfigConstants.identificatorParamName%>: $('#<%= ConfigConstants.identificatorParamName%>').val(),
			<%= ConfigConstants.entitiesBanManagementPageTypeOfEntityParam%>: $('#<%= ConfigConstants.entitiesBanManagementPageTypeOfEntityParam%>').val(),
			<%= ConfigConstants.typeOfIdentificatorParamName%>: $('#<%= ConfigConstants.typeOfIdentificatorParamName%>').val(),
			<%= ConfigConstants.bansFirstIDFromFirstSetParamName%>: $('#<%= ConfigConstants.bansFirstIDFromFirstSetParamName%>').val() == undefined ? null : $('#<%= ConfigConstants.bansFirstIDFromFirstSetParamName%>').val()},
		success: 
			function(data) { // can contain errors instead of user bans
				//prompt("Copy to clipboard: Ctrl+C, Enter", data);
				$('#bansContainer').append(data);
	  		}
		})
}
</script>
</head>
<body>

<a href="<%= WebConstants.servletPrefix + WebConstants.adminToolsMenuURL%>"><spring:message code="ToMenu"/></a><br/>
<%
if(typeOfEntity == TypesOfEntity.IP_ADDRESSES){
%>
<spring:message code="Login"/>: <input id="<%= TypesOfIdentificator.LOGIN.getTypeName()%>"/><br/>
<input value="<spring:message code="GetLoginIP"/>" onclick="loadIPAddress()" type="button"/>
<div id="ipContainer"></div>
<%
}
%>
<form action="<%= WebConstants.servletPrefix + WebConstants.adminToolsMenuURL + "/" + WebConstants.banURL%>" method="post">
	<%= identificatorName%>:<input id="<%= ConfigConstants.identificatorParamName%>" name="<%= ConfigConstants.identificatorParamName%>"><br/>
	<% 
	if(entityDoesntExistMsg != null){
	%>
	<h5 style="color: red"><%= entityDoesntExistMsg %></h5><br/>
	<% 
	}
	if(illegalIdentificatorMsg != null){
	%>
	<h5 style="color: red"><%= illegalIdentificatorMsg %></h5><br/>
	<% 
	}
	%>
	
	<spring:message code="AddBan"/>:<br/>
	<spring:message code="ToForExampleDateMsg"/>:
	<input type="date" name="<%= ConfigConstants.entitiesBanManagementPageToDateParam%>"/><br/>
	<%
	if(toDateErrorMsg != null){
	%>
	<h5 style="color: red"><%= toDateErrorMsg %></h5><br/>
	<% 
	}
	if(mayBeDidntSetDateMsg != null){
	%>
	<h5 style="color: red"><%= mayBeDidntSetDateMsg %></h5><br/>
	<% 
	}
	if(fromOrToDateParseExceptionMsg != null){
	%>
	<h5 style="color: red"><%= fromOrToDateParseExceptionMsg %></h5><br/>
	<% 
	}
	%>
	<spring:message code="Forever"/><input type="checkbox" name="<%= ConfigConstants.entitiesBanManagementPageForeverCheckboxParam%>"/>
	<input type="hidden" id="<%= ConfigConstants.entitiesBanManagementPageTypeOfEntityParam%>" name="<%= ConfigConstants.entitiesBanManagementPageTypeOfEntityParam%>" value="<%= typeOfEntity.getTypeName()%>"/>
	<input type="hidden" id="<%= ConfigConstants.typeOfIdentificatorParamName%>" name="<%= ConfigConstants.typeOfIdentificatorParamName%>" value="<%= typeOfIdentificator.getTypeName()%>"/>
	<input type="submit" value="<spring:message code="ToBan"/>"/><br/><br/>
	
	<div id="bansContainer"></div><br/><br/>
	<input type="button" id="showBansButton" onclick="loadBans()" value="<spring:message code="ToShowBans"/>"/>
</form>

</body>
</html>