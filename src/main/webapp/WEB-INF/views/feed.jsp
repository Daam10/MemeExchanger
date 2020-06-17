<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="ru.miron.Config.WebConstants, ru.miron.Config.ConfigConstants"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><spring:message code="Feed"/></title>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

<script type="text/javascript">
var postsCountToGet = 5;
var commentsCountToGet = 5;
var postsCountFrom = 0;
var postsCountTo = postsCountToGet;
var isFirstSet = new Boolean(true);

function loadMore(){
	$.ajax({
		url: "<%= WebConstants.getFeedURL %>", 
		data: {<%= ConfigConstants.countFromParamName%>: postsCountFrom.valueOf(),
			<%= ConfigConstants.countToParamName%>: postsCountTo.valueOf(),
			<%= ConfigConstants.postsFirstIDFromFirstSetParamName%>: $('#<%= ConfigConstants.postsFirstIDFromFirstSetParamName%>').val() == undefined ? null : $('#<%= ConfigConstants.postsFirstIDFromFirstSetParamName%>').val()},
		success: 
			function(data) {
	    		$('#postsContainer').append(data);
	  		}
		})
}

function divStyleSetter(){
	var pageWidth = document.documentElement.clientWidth;
	var pageHeight = document.documentElement.clientHeight;
	
	if(pageWidth > pageHeight){
		$("#postsContainer").css("width", pageHeight);
	} else {
		$("#postsContainer").css("width", pageWidth);
	}
}


window.onresize = divStyleSetter;
window.onload   = divStyleSetter;
</script>
</head>
<body>
<a href="<%= WebConstants.servletPrefix +  WebConstants.mainURL%>"><spring:message code="ToMenu"/></a><br/>
<div style="min-width: 300px" id="postsContainer"></div>
<button onclick="loadMore()"><spring:message code="LoadMore"/></button>
</body>
</html>