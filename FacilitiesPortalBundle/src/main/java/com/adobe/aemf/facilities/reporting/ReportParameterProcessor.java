package com.adobe.aemf.facilities.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.util.PortalUtils;

public class ReportParameterProcessor {
	private Map requestParameters;
	private List<ReportRequest> reportRequests = new ArrayList<ReportRequest>();
	
	private void init(){
		int sheetCount = getSheetsCountFromRequest();
		createReportRequests(sheetCount);
	}
	private void createReportRequests(int sheetCount) {
		if(sheetCount == 0) 
			throw new IllegalArgumentException();
		
		for (int i = 0; i < sheetCount; i++) {
			Map repParams = getReportParameters(i);
			ReportRequest rr = initReportRequest(repParams);
			addToList(rr);
		}
		
	}

	private void addToList(ReportRequest rr) {
		if(rr != null)
		reportRequests.add(rr);
	}

	private ReportRequest initReportRequest(Map repParams) {
		return new ReportRequest(repParams);
	}

	private Map getReportParameters(int reportCount) {
		Map repReqParams = new HashMap();
		String sheetName = PortalUtils.parseStringWithDefault(((String[]) requestParameters.get(SharedConstants.WORKSHEET+reportCount))[0],SharedConstants.WORKSHEETDEFAULTNAME);
		String surveyId = PortalUtils.parseString(((String[]) requestParameters.get(SharedConstants.SURVEY_ID+reportCount))[0]);
		boolean rawData = false;
		if(requestParameters.get(SharedConstants.RAWDATA+reportCount) != null){
			rawData = true;
		}
		
		if(surveyId == ""  )
			throw new IllegalArgumentException("Survey ID not specified !");
		
		repReqParams.put(SharedConstants.WORKSHEET, sheetName);
		repReqParams.put(SharedConstants.SURVEY_ID, surveyId);
		repReqParams.put(SharedConstants.RAWDATA, rawData);
		return repReqParams;
	}
	
	private int getSheetsCountFromRequest() {
		String sheetCountParamValue = ((String[]) requestParameters.get(SharedConstants.WORKSHEETCOUNT))[0];
		int sheetCount = NumberUtils.toInt(sheetCountParamValue);
		return sheetCount;
	}

	public List<ReportRequest> getReportRequests() {
		return reportRequests;
	}

	public void process(Map parameterMap) throws IllegalArgumentException{
		if(parameterMap == null || parameterMap.isEmpty()) 
			throw new IllegalArgumentException();
		requestParameters = parameterMap;
		init();
	}

}
