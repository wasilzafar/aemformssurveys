package com.adobe.aemf.facilities.dashboard;

import java.util.HashMap;
import java.util.Map;

import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.util.PortalUtils;

public class DashboardParameterProcessor {
	
	Map requestParameters;
	
	Map parameters;
	
	public void process(Map parameterMap) {
		if(parameterMap == null) throw new IllegalArgumentException();
		requestParameters = parameterMap;
		init();
	}

	private void init() {
		Map reqParams = new HashMap();
		String surveyId = null;
		String[] surIdParamValue = ((String[]) requestParameters.get(SharedConstants.SURVEY_ID));
		if(surIdParamValue != null){
		surveyId = PortalUtils.parseStringWithDefault(surIdParamValue[0],"UNKNOWN");
		reqParams.put(SharedConstants.SURVEY_ID, surveyId);
		}
		
		
		parameters = reqParams;
		
	}

	public Map getDashboardDataRequests() {
		return parameters;
	}

}
