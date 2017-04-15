package com.adobe.aemf.facilities.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.search.SurveyDataSearch;
import com.adobe.aemf.facilities.util.JSONUtils;
import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.um.UserIdentity;

@SlingServlet(paths = {"/bin/fp/surveysub","/bin/fp/del/surveysub","/bin/fp/view"}, methods = "GET,POST")
@Service(Servlet.class)
public class SurveySubmisssionsServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	Logger logger = LoggerFactory.getLogger(SurveySubmisssionsServlet.class);
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	

	@Reference
	PortalConfigComponent configAdmin;

	@Reference
	private SurveyDataSearch surveyDataSearch;
	
	@Activate
	@Modified
	protected void activate(ComponentContext ctx) {
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
		HttpSession session = request.getSession(false);
		logger.debug("Found a session with ID " + session.getId());
		UserIdentity userId = (UserIdentity) session
				.getAttribute(SharedConstants.SESSION_USER_ATTRIBUTE);
		String path = request.getRequestURI();
		if (path.equals("/bin/fp/surveysub")) {
			String surveyId = request.getParameter(SharedConstants.SURVEY_ID);
		if (surveyId != null) {
			Map responseMap;
			try {
				responseMap = surveyDataSearch.getSurveyData(surveyId, request);
				serveResponse(response, JSONUtils.generateJSON(responseMap));
			} catch (Exception e) {
				logger.error("Error while fetching survey data !");
			}
		}
			
		}else if(path.equals("/bin/fp/del/surveysub")){
			
		}else if(path.equals("/bin/fp/view")){
			String submissionId = request.getParameter(SharedConstants.SUBMISSION_NODE);
			String dataPath = "/";
			try {
				dataPath = surveyDataSearch.getSumissionDataPath(submissionId);
			} catch (Exception e) {
				logger.error("Error while redirecting to view URL  ");
			}
			logger.debug("Data  path found : "+dataPath);
			String view = createViewURL(dataPath);
			response.sendRedirect(view);
		}
	}
	
	private String createViewURL(String dataPath) {
		String formPath = null;
		if (dataPath != null) {
			String formPathSubString = StringUtils.substringAfter(dataPath, SharedConstants.USER_GENERATED_CONTENT_FOLDER);
			formPath = StringUtils.substring(formPathSubString, 0, StringUtils.ordinalIndexOf(formPathSubString, SharedConstants.PATH_SEPARATOR, 7));
		}
		
		StringBuffer finalURL= new StringBuffer(configAdmin.getBaseURL());
		finalURL.append(formPath.substring(1));
		finalURL.append(SharedConstants.HTML_EXTENSION);
		finalURL.append("?wcmmode=disabled&dataRef=crx://");
		finalURL.append(dataPath);
		logger.debug("Form view URL : "+finalURL);
		return finalURL.toString();
	}

	private void serveResponse(SlingHttpServletResponse response,
			JSONObject outJSON) throws JSONException, IOException {
		logger.debug("Returned JSON response : " + outJSON);
		response.setContentType("application/json");
		outJSON.write(response.getWriter());

	}
	


}
