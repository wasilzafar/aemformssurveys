package com.adobe.aemf.facilities.util;

import com.adobe.aemf.facilities.core.SharedConstants;

public class PathBuilder {
	
	public static String createdFormPathForSurvey(String surveyId, String formPath){
		
		return SharedConstants.USER_GENERATED_CONTENT_FOLDER+formPath+"/"+surveyId;
		
	}

}
