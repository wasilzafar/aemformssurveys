package com.adobe.aemf.facilities.reporting;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.adobe.aemf.facilities.reporting.ReportParameterProcessor;

public class ReportParameterProcessorTest {
	ReportParameterProcessor processor = new ReportParameterProcessor();

	@Test(expected=IllegalArgumentException.class)
	public void testNullMap() {
		Map params = null;
		processor.process(params);
	}
	
	@Test
	public void testNullMapGetReports() {
		Map params = null;
		assertNotNull("Reports should be null with null map", processor.getReportRequests());
	}

}
