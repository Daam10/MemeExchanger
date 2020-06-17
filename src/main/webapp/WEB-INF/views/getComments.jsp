<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.ConfigConstants, ru.miron.Entities.PostCommentWithBanInfo, java.util.*, ru.miron.Config.WebConstants, java.util.Date, ru.miron.Utils.CommonUtils, ru.miron.Config.DBConstants"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

<%
List<PostCommentWithBanInfo> comments = (List<PostCommentWithBanInfo>) request.getAttribute(ConfigConstants.getCommentsPageCommentsAttrName);
Integer postID = (Integer) request.getAttribute(ConfigConstants.postIDAttrName);
boolean isFirstSet = (Boolean) request.getAttribute(ConfigConstants.isFirstSetAttrName);
%>

<% 
if(comments == null || comments.isEmpty()){
%>
<!--<spring:message code="NoComments"/>-->
<%
} else {
	for(PostCommentWithBanInfo comment : comments){
		if(isFirstSet){
		%>
		<spring:message code="Id"/>:<span id="<%= ConfigConstants.commentsFirstIDFromFirstSetParamName%>"><%= comment.getId() %></span><br/>
		<%
		} else { 
		%>
		<spring:message code="Id"/>:<%= comment.getId() %><br/>
		<% 	
		}
		%>
		<spring:message code="Login"/>:<%= comment.getLogin() %><br/>
		<% 
		if(comment.isBanned()){
		%>
		<spring:message code="CommentBanned"/>: 
		<spring:message code="fromDateForExampleMsg"/> <input type="date" value="<%= WebConstants.htmlDateFormater.format(comment.getBannedFrom())%>" readonly/> 
		<spring:message code="toDateForExampleMsg"/> <input type="date" value="<%= WebConstants.htmlDateFormater.format(comment.getBannedTo())%>" readonly/><br/>
		<% 
		} else {
		%>
		<textarea rows="<%=(int) Math.ceil(DBConstants.MAX_COMMENT_LENGTH / ConfigConstants.COMMENT_COLUMNS_COUNT)%>" cols="<%=ConfigConstants.COMMENT_COLUMNS_COUNT%>" readonly><%= comment.getText()%></textarea>
		<br/>
		<%
		}
		%>
		
<% 
	}
	%>
	<script>
		commentsCountTo<%= postID%> += <%= comments.size()%>;
		commentsCountFrom<%= postID%> += <%= comments.size()%>;
	</script>
	<%
}
%>
