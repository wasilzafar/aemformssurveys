package com.adobe.aemf.facilities.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.exceptions.PortalException;
import com.adobe.aemf.facilities.reporting.JSONBasedXSSReportGenerator;
import com.adobe.aemf.facilities.reporting.Report;
import com.adobe.aemf.facilities.reporting.ReportGenerator;
import com.adobe.aemf.facilities.reporting.ReportParameterProcessor;
import com.adobe.aemf.facilities.reporting.ReportRequest;
import com.adobe.aemf.facilities.um.UserIdentity;

@SlingServlet(paths = {"/bin/fp/repgen/submit"}, methods = "GET,POST")
/**
 * Servlet responsible for handling report generation requests.
 * @author zafar
 *
 */
@Service(Servlet.class)
public class ReportGeneratorServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	Logger logger = LoggerFactory.getLogger(ReportGeneratorServlet.class);

	@Reference
	JSONBasedXSSReportGenerator reportGenerator;
	
	@Activate
	@Modified
	protected void activate(ComponentContext ctx) {
	}

	@Override
	public void init() throws ServletException {
	}
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		
			String path = request.getRequestURI();
			HttpSession session = request.getSession(false);
			logger.debug("Found a session with ID " + session.getId());
			UserIdentity userId = (UserIdentity) session
					.getAttribute(SharedConstants.SESSION_USER_ATTRIBUTE);
			
		if (path.equals("/bin/fp/repgen/submit")) {
			List<ReportRequest> reportsRequired = processParameters(request);
			Report report = null;
			try {
				report = reportGenerator.generateReport(reportsRequired, request);
			} catch (PortalException e) {
				logger.error(
						"Error while generating the report; nested exception ",
						e.getMessage());
			}
			serveResponse(response, report);
		}

	}

	private void serveResponse(SlingHttpServletResponse response, JSONObject outJSON) throws JSONException, IOException {
		logger.debug("Returned JSON response : " + outJSON);
		response.setContentType("application/json");
		outJSON.write(response.getWriter());
	}
	
	private void serveResponse(SlingHttpServletResponse response, Report report)
			throws IOException {
		report.disposeToHttpServletResponse(response);
		response.flushBuffer();
	}

	private List<ReportRequest> processParameters(SlingHttpServletRequest request) {
		ReportParameterProcessor processor = new ReportParameterProcessor();
		processor.process(request.getParameterMap());
		return processor.getReportRequests();
	}
}
