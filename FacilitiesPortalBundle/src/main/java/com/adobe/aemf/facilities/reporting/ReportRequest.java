package com.adobe.aemf.facilities.reporting;

import java.util.List;
import java.util.Map;

import com.adobe.aemf.facilities.core.SharedConstants;

public class ReportRequest {
	List filter;
	
	private String workBookName;
	private String surveyId;
	private boolean rawData;
	
	public ReportRequest(Map param) {
		initialize(param);
	}

	private void initialize(Map param) {
		this.workBookName = (String) param.get(SharedConstants.WORKSHEET);
		this.surveyId = (String) param.get(SharedConstants.SURVEY_ID);
		this.rawData = (boolean)param.get(SharedConstants.RAWDATA);
	}

	public String getWorkBookName() {
		return workBookName;
	}

	public List getFilter() {
		return filter;
	}

	public void setFilter(List filter) {
		this.filter = filter;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public boolean isRawData() {
		return rawData;
	}

	public void setRawData(boolean rawData) {
		this.rawData = rawData;
	}
	
}
