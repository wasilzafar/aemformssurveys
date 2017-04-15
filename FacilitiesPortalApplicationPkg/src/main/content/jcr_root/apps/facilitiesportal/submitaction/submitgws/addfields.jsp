<%------------------------------------------------------------------------
 ~
 ~ ADOBE CONFIDENTIAL
 ~ __________________
 ~
 ~  Copyright 2014 Adobe Systems Incorporated
 ~  All Rights Reserved.
 ~
 ~ NOTICE:  All information contained herein is, and remains
 ~ the property of Adobe Systems Incorporated and its suppliers,
 ~ if any.  The intellectual and technical concepts contained
 ~ herein are proprietary to Adobe Systems Incorporated and its
 ~ suppliers and may be covered by U.S. and Foreign Patents,
 ~ patents in process, and are protected by trade secret or copyright law.
 ~ Dissemination of this information or reproduction of this material
 ~ is strictly forbidden unless prior written permission is obtained
 ~ from Adobe Systems Incorporated.
 --------------------------------------------------------------------------%>
<%@include file="/libs/fd/af/components/guidesglobal.jsp"%>
<%@page import="com.adobe.aemds.guide.servlet.GuideSubmitServlet,
                org.apache.sling.commons.json.JSONObject,
                org.slf4j.Logger,
                org.slf4j.LoggerFactory" %>

<%@ page import="java.util.*" %>
<%@ page import="java.nio.charset.Charset" %>
<%@ page import="java.net.URLDecoder" %>

<%@ page import="org.apache.sling.api.request.RequestParameter" %>
<%@ page import="org.apache.sling.api.SlingHttpServletResponse" %>

<%@ page import="com.adobe.aemds.guide.utils.GuideUtils" %>
<%@ page import="com.adobe.forms.common.submitutils.CustomResponse" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.adobe.aemf.facilities.survey.Surveyor" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.day.cq.personalization.UserPropertiesUtil" %>
<%@ page import="com.adobe.granite.security.user.UserProperties" %>
<%@ page import="org.apache.sling.commons.json.JSONObject" %>
<%@ page import="org.apache.sling.api.request.RequestParameter" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="org.apache.jackrabbit.util.Base64" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="org.apache.sling.api.auth.Authenticator"%>

<%!
    private final Logger log = LoggerFactory.getLogger(getClass());
%>


<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<sling:defineObjects/>

<input type="hidden" id="guideValueMap" name="_guideValueMap" value="yes"/>

<% 

log.info("From addFields JSP");

final boolean isAnonymous = UserPropertiesUtil.isAnonymous(slingRequest);
log.info(" Is form user anonymous : "+ isAnonymous);
boolean isDisabled = WCMMode.fromRequest(request).equals(WCMMode.DISABLED);

if(!isAnonymous && slingRequest.getParameter("dataRef")== null && slingRequest.getAttribute("data")==null){

    RequestParameter sIdParam = slingRequest.getRequestParameterMap().getValue("surveyId");
    String sId = null;
    if(sIdParam != null) sId = sIdParam.getString();
	if( sId != null){
	NumberFormat f = NumberFormat.getInstance();
	f.setGroupingUsed(false);
	long dbl = Long.parseLong(sId,16);
	sId = f.format(dbl);
    Surveyor surveyor = sling.getService(Surveyor.class);
	boolean isActive = surveyor.isSurveyActive(sId);
	log.info("Is survey "+ sIdParam +" Active : "+isActive);

        /*!-- Insert prefilled fields only if survey is active --*/
	if(slingRequest.getParameter("surveyId") != null && isActive){
		%>
<input type="hidden" id="surveyId" name="surveyId" value="<%=sId %>" />
<%

    UserProperties userProperties = slingRequest.adaptTo(UserProperties.class);
    String[] names = userProperties.getPropertyNames();
	for (String name : names) {
		log.info("Property name  : "+name);
	}
	
	String loginId = userProperties.getAuthorizableID();	
	String familyName = userProperties.getProperty("familyName");	
	String givenName = userProperties.getProperty("givenName");


	
	String fullUserName;
	if (givenName != null && familyName != null) {
		fullUserName = givenName + " " + familyName;
	} else if (givenName != null && familyName == null) {
		fullUserName = givenName;
	} else if (givenName == null && familyName != null) {
		fullUserName = familyName;
	} else {
		fullUserName = loginId;
	}


	log.info("loginId::familyName::givenName::fullUserName :: :: "+loginId+"::"+familyName+"::"+givenName+"::"+fullUserName);

	if(properties.get("ldapId", (String)null) != null && loginId != null){
		%>
<input type="hidden" name="ldapId" value="<%=loginId %>" />
<%
	}
	
	if(properties.get("fullUserName", (String)null) != null && fullUserName != null){
		%>
<input type="hidden" name="fullUserName" value="<%=fullUserName %>" />
<%
	}
	
	String email = userProperties.getProperty("email");
	log.info("email  : "+email);
	if(properties.get("userEmail", (String)null) != null && email != null){
		%>
<input type="hidden" name="email" value="<%= email %>" />
<%
	}


   }

  }
}

%>