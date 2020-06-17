<%@ page language="java" contentType="text/html"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants"%>
<%@ page import="java.util.List,ru.miron.Entities.FeedPostWithBanInfo,ru.miron.Config.WebConstants,ru.miron.Config.DBConstants" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%

List<FeedPostWithBanInfo> feedPostsWithBanInfo = (List<FeedPostWithBanInfo>) request.getAttribute(ConfigConstants.feedPostsWithBanInfoAttrName);
String loginFromCookies = (String) request.getAttribute(ConfigConstants.loginAttrName);
boolean canLeaveComments = (Boolean) request.getAttribute(ConfigConstants.getFeedPageCanLeaveCommentsAttrName);
boolean isFirstSet = (Boolean) request.getAttribute(ConfigConstants.isFirstSetAttrName);
for(FeedPostWithBanInfo post : feedPostsWithBanInfo){
	if(isFirstSet){
	%>
	<spring:message code="Id"/>:<span id="<%= ConfigConstants.postsFirstIDFromFirstSetParamName%>"><%= post.getId() %></span><br/>
	<%
	} else { 
	%>
	<spring:message code="Id"/>:<%= post.getId() %><br/>
<% 	}%>
<spring:message code="AuthorsLogin"/>:<%= post.getLogin() %><br/>
<spring:message code="PublishDate"/>:<%= post.getPublishDate() %><br/>
<%	
	if(post.isBanned()){
	%>
	<spring:message code="PostBanned"/>: 
	<spring:message code="fromDateForExampleMsg"/> <input type="date" value="<%= WebConstants.htmlDateFormater.format(post.getBannedFrom())%>" readonly/> 
	<spring:message code="toDateForExampleMsg"/> <input type="date" value="<%= WebConstants.htmlDateFormater.format(post.getBannedTo())%>" readonly/><br/>
	<%
	} else {
		if(post.isTitleExists()){
			%>
<spring:message code="Title"/>:<%= post.getTitle() %><br/>
<% 
		}
%>
<img width="100%" alt="<%= post.getId() %>" src="<%= WebConstants.imgsURL + "?" + ConfigConstants.imgNameParamName + "=" + post.getPictureName() /*Разобраться с ссылками*/ %>"><br/>

<div id="commentsContainer<%= post.getId()%>"></div>
<input type="button" onclick="loadComments<%= post.getId()%>()" value="<spring:message code="LoadComments"/>"/><br/>
	<%
	if(canLeaveComments){
	%>
	<textarea id="<%= ConfigConstants.getFeedTextOfCommentParamName + post.getId()%>" rows="<%=(int) Math.ceil(DBConstants.MAX_COMMENT_LENGTH / ConfigConstants.COMMENT_COLUMNS_COUNT)%>" cols="<%=ConfigConstants.COMMENT_COLUMNS_COUNT%>" maxlength="<%= DBConstants.MAX_COMMENT_LENGTH%>"></textarea>
	<br/><input type="button" onclick="leaveComment<%= post.getId()%>()" value="<spring:message code="LeaveComment"/>"/><br/>
	<% 
	} else {
	%>
	<h5 style="color: red"><spring:message code="CannotLeaveCommentMsg"/></h5> 
	<%
	}
	%>
<script>
var commentsCountFrom<%= post.getId()%> = 0;
var commentsCountTo<%= post.getId()%> = commentsCountToGet;
function loadComments<%= post.getId()%>(){
	$.ajax({
		url: "<%= WebConstants.getCommentsURL %>", 
		data: {<%= ConfigConstants.countFromParamName%>: commentsCountFrom<%= post.getId()%>.valueOf(),
			<%= ConfigConstants.countToParamName%>: commentsCountTo<%= post.getId()%>.valueOf(),
			<%= ConfigConstants.commentsFirstIDFromFirstSetParamName%>: $('#<%= ConfigConstants.commentsFirstIDFromFirstSetParamName%>').val() == undefined ? null : $('#<%= ConfigConstants.commentsFirstIDFromFirstSetParamName%>').val(),
			<%= ConfigConstants.postIDParamName%>: <%= post.getId()%>},
		success: 
			function(data) {
	    		$('#commentsContainer<%= post.getId()%>').append(data);
	  		}
		})
}

function leaveComment<%= post.getId()%>(){
	$.ajax({
		url: "<%= WebConstants.createCommentURL %>", 
		data: {<%= ConfigConstants.getFeedTextOfCommentParamName%>: $('#<%= ConfigConstants.getFeedTextOfCommentParamName + post.getId()%>').val(),
			<%= ConfigConstants.postIDParamName%>: <%= post.getId()%>},
		method: "POST",
		success:
			function(data) {
				$('#<%= ConfigConstants.getFeedTextOfCommentParamName + post.getId()%>').val("");
			}
		})
}
</script>
<%
	}
}
%>
<script>
	postsCountTo += <%= feedPostsWithBanInfo.size()%>;
	postsCountFrom += <%= feedPostsWithBanInfo.size()%>;
</script>