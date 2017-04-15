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

<%@include file="/libs/fd/af/components/guidesglobal.jsp" %>
<%@page import="com.adobe.aemds.guide.servlet.GuideSubmitServlet,
                org.json.JSONObject,
                org.slf4j.Logger,
                org.slf4j.LoggerFactory" %>

<%@ page import="java.util.*" %>
<%@ page import="java.nio.charset.Charset" %>
<%@ page import="java.net.URLDecoder" %>

<%@ page import="org.apache.sling.api.request.RequestParameter" %>
<%@ page import="org.apache.sling.api.SlingHttpServletRequest" %>
<%@ page import="org.apache.sling.api.SlingHttpServletResponse" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="javax.jcr.Session" %>


<%@ page import="com.adobe.aemds.guide.utils.GuideUtils" %>
<%@ page import="com.adobe.aemf.facilities.survey.SurveySubmissionHandler" %>
<%@ page import="com.adobe.aemf.facilities.util.JSONUtils" %>

<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<sling:defineObjects/>

<% 
final Logger jspLogger = LoggerFactory.getLogger(getClass());
SurveySubmissionHandler handler = sling.getService(SurveySubmissionHandler.class);
Session userSession = ((SlingHttpServletRequest)request).getResourceResolver().adaptTo(Session.class);
String surveyId, path, guideValuesMap, jcrData; // Mandatory parameters
String prefillConfigEmail, prefillConfigLdapId, prefillConfigFullUserName;
String email, ldapId, fullUserName; // Pre-fill parameters
%>
<%
	// Log and capture few important parameters
	
	path = currentPage.getPath();
	jspLogger.info("Current page path : "+ path);
	jcrData = request.getParameter("jcr:data");
	jspLogger.info("JCR Data : "+jcrData);
	surveyId = request.getParameter("surveyId");
	jspLogger.info("Survey ID : "+surveyId);
	guideValuesMap = request.getParameter("_guideValuesMap");
	jspLogger.info("_guideValuesMap : "+guideValuesMap);	
	
	// Done with logging and value capturing .............

	
    
	if(slingRequest.getParameter("_guideValuesMap")!= null && surveyId != null) {
		Map dataMap = new HashMap();
        JSONObject guideValueMap = new JSONObject(slingRequest.getParameter("_guideValuesMap"));
        dataMap = JSONUtils.jsonToMap(guideValueMap);
        // Remove extra submit* parameter present in guideValueMap
        String matchedKey = "";
	    for (Iterator iterator = dataMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			jspLogger.info("Logging keys  "+key);
			if(key.startsWith("submit")){
				matchedKey = key;
			break;
			}
		}
		jspLogger.info("Removing key  "+matchedKey);
	    dataMap.remove(matchedKey);
	    
	    dataMap.put("jcr:data", jcrData);
	    dataMap.put("surveyId", surveyId);
	    
    	// Log and capture prefill values in request parameters 
    	
    	prefillConfigEmail = properties.get("userEmail", (String)null);
    	jspLogger.info("Prefill Email : "+prefillConfigEmail);
    	if(prefillConfigEmail != null){
    		email = request.getParameter("email");
    		dataMap.put("email", email);
    		jspLogger.info("Email "+email);
    	}
    	
    	prefillConfigLdapId = properties.get("ldapId", (String)null);
    	jspLogger.info("Prefill ID : "+prefillConfigLdapId);
    	if(prefillConfigLdapId != null){
    		ldapId = request.getParameter("ldapId");
    		dataMap.put("loginId", ldapId);
    		jspLogger.info("LDAP "+ldapId);
    	}

    	prefillConfigFullUserName = properties.get("fullUserName", (String)null);
    	jspLogger.info("Prefill Full Name : "+prefillConfigFullUserName);
    	if(prefillConfigFullUserName != null){
    		fullUserName = request.getParameter("fullUserName");
    		dataMap.put("completeName", fullUserName);
    		jspLogger.info("Full Name "+fullUserName);
    	}

    	boolean isSuccessful = handler.handleSubmission(path,dataMap,userSession);
		jspLogger.info("isSuccessful : "+isSuccessful);
        userSession.save();
    }
	

%>