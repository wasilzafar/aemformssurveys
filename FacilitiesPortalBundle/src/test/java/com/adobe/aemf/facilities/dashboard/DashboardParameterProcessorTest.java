package com.adobe.aemf.facilities.dashboard;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DashboardParameterProcessorTest {
	private static final String OPERATION = "op";
	private static final String GEOGRAPHY = "geo";
	private static final String FORMNAME = "form";
	private static final String PARAMETER = "parameter";
	private static final String FROM = "fromDate";
	private static final String TO = "toDate";
	Map params;
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
@Ignore
	@Test
	public void test_process_operationParamValid() {}
@Ignore	
	@Test
	public void test_process_formParamValid() {
		params = new HashMap();
		String testName = "Sample_Form";
		params.put(FORMNAME, new String[]{testName});
		DashboardParameterProcessor processor = new DashboardParameterProcessor();
		processor.process(params);
		String form = (String) processor.getDashboardDataRequests().get(FORMNAME);
		assertTrue(
				form.equalsIgnoreCase(testName));
		params.clear();
	}

}
