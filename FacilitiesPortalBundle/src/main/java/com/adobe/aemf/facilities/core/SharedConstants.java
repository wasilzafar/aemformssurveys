package com.adobe.aemf.facilities.core;

import java.util.HashSet;
import java.util.Set;

public interface SharedConstants {
	
public static final String JCR_CREATED = "jcr:created";	
public static final String SERVICE_VENDOR = "Adobe Systems";
public static final String FORM_NAME = "formname";
public static final String START_DATE = "startdate";
public static final String END_DATE = "enddate";
public static final String PATH_SEPARATOR = "/";



public static final String WORKSHEET = "wsname";
public static final String RAWDATA = "rawData";
public static final String WORKSHEETCOUNT = "sheetcount";
public static final String WORKSHEETDEFAULTNAME = "Worksheet";


public static final String SURVEY_ID = "surveyId";
public static final String SURVEY_DESCRIPTION = "description";
public static final String SURVEY_EMAIL_MESSAGE = "message";
public static final String SURVEY_EMAIL_SUBJECT = "subject";
public static final String SURVEY_PARTICIPANTS = "participants";
public static final String SURVEY_FORM = "surveyForm";
public static final String SURVEY_CREATEDBY = "createdby";
public static final String SURVEY_GEO = "geo";
public static final String SURVEY_URL = "url";
public static final String SURVEY_DURATION = "duration";
public static final String SURVEY_STATUS = "status";
public static final String SESSION_USER_ATTRIBUTE = "sessionuser";
public final static String SURVEY_ID_PREFIX = "SRV-";

public final static String SURVEY_STATUS_ACTIVE = "ACTIVE";
public final static String SURVEY_STATUS_DEACTIVATED = "DEACTIVATED";
public final static String SURVEY_STATUS_COMPLETE = "COMPLETE";


public final static String DATA_NODE_ADDED = "resourcePath";
public static final String FORM_DATA_PROPERTY_VALUE_POOR = "Poor";
public static final String NAME = "name";
public static final String CRITICAL_NODE_NAME = "nodeName";
public static final String IS_CRITICAL = "isCritical";
public static final String SUBMITTED_BY = "user";
public static final String CRITICAL_ID = "criticalId";
public static final String USER_GENERATED_CONTENT_FOLDER = "/content/usergenerated";
public static final String GUIDE_CONTAINER_SUFFIX = "/jcr:content/guideContainer";

public static final Set<String> PROPERTIESTOIGNORE = new HashSet<String>() {{
    add("jcr:created");
    add("jcr:createdBy");
    add("jcr:primaryType");
}};
public static final String SUBMISSION_NODE = "submissionNode";
public static final Object HTML_EXTENSION = ".html";
public static final Object QUESTION_MARK = "?";
public static final Object QUERY_PARAMETER_SEPARATOR = "&";
public static final Object WCM_DISABLED = "wcmmode=disabled";

}
