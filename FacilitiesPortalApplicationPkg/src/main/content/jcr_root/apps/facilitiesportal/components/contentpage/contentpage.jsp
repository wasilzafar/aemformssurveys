<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.adobe.aemf.facilities.um.UserIdentity,javax.servlet.http.HttpSession" %>
<cq:defineObjects/>
<!--<%@page session="false" contentType="text/html; charset=utf-8" %>--><%
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>GWS Portal</title>
    <%currentDesign.writeCssIncludes(pageContext); %>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/>
    <cq:include script="/libs/wcm/mobile/components/simulator/simulator.jsp"/>
    
</head>
<body>
    <a href="/content/GWSforms/en.html"><img alt="" src='<%= currentDesign.getPath() +"/images/FixedTop.png"%>' id="full-screen-background-image" /></a> 
    <div style="position:absolute;top:1%;right:1%">
<%
    HttpSession session = slingRequest.getSession(false); 
	UserIdentity uId = (UserIdentity)session.getAttribute("sessionuser");
	if(uId != null){
		out.println("Welcome , "+uId.getFullName());
    }else {

    }
	out.println();
%></div>
<div id="wrapper">
	<div id="menuContainer" >
    <cq:include script="topnav.jsp"/>
    </div>
    <div id="bodyContainer" >
    <cq:include script="body.jsp"/>
    </div>
</div>
</body>
</html>