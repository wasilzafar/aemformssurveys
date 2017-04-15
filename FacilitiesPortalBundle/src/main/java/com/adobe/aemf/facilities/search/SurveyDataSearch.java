package com.adobe.aemf.facilities.search;

import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;

public interface SurveyDataSearch {

	public Map getSurveyData(String id, SlingHttpServletRequest request) throws Exception;
	
	public Map getSurveyDataStatistics(String id,SlingHttpServletRequest request) throws Exception;
	
	public String getSumissionDataPath(String dataNodeName) throws Exception;
	
}
