package com.adobe.aemf.facilities.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Deactivate;
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

import com.adobe.aemf.facilities.util.JSONUtils;
import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.PortalDataAccessManager;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.survey.SurveyDTO;
import com.adobe.aemf.facilities.survey.SurveyIdGenerator;
import com.adobe.aemf.facilities.survey.SurveyRequestParamsHandler;
import com.adobe.aemf.facilities.survey.Surveyor;
import com.adobe.aemf.facilities.um.UserIdentity;

@SlingServlet(paths = { "/bin/fp/survey/create", "/bin/fp/survey/geos",
		"/bin/fp/survey/forms", "/bin/fp/survey/list", "/bin/fp/survey/getId",
		"/bin/fp/survey/getData", "/bin/fp/survey/update","/bin/fp/survey/delete" }, methods = "GET,POST")
@Service(Servlet.class)
public class SurveyHandlerServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	Logger logger = LoggerFactory.getLogger(SurveyHandlerServlet.class);

	@Reference
	Surveyor surveyor;

	@Reference
	PortalDataAccessManager pfm;

	@Reference
	PortalConfigComponent portalConfig;

	@Reference
	ResourceResolverFactory resolverFactory;

	@Reference
	SurveyIdGenerator surveyIdGenerator;

	@Activate
	@Modified
	protected void activate(ComponentContext ctx) {
	}

	@Deactivate
	protected void deactivate(ComponentContext ctx) {
	}

	public void init() throws ServletException {
	}

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		doPost(request, response);
	}

	private void serveResponse(SlingHttpServletResponse response,
			JSONObject outJSON) throws JSONException, IOException {
		logger.debug("Returned JSON response : " + outJSON);
		response.setContentType("application/json");
		outJSON.write(response.getWriter());

	}

	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		String path = request.getRequestURI();
		HttpSession session = request.getSession(false);
		logger.debug("Found a session with ID " + session.getId());
		UserIdentity userId = (UserIdentity) session
				.getAttribute(SharedConstants.SESSION_USER_ATTRIBUTE);
		try {
			if (path.equals("/bin/fp/survey/geos")) {
				List geos;
				geos = pfm.getGeos();
				responseMap.put("geos", geos);
				serveResponse(response, JSONUtils.generateJSON(responseMap));
			} else if (path.equals("/bin/fp/survey/getId")) {
				String id;
				id = surveyIdGenerator.createID();
				responseMap.put("id", id);
				serveResponse(response, JSONUtils.generateJSON(responseMap));
			} else if (path.equals("/bin/fp/survey/forms")) {
				List<String> geoForms = new ArrayList<String>();
				String geo = userId.getGeo();
				Map formsMap = pfm.getAllForms();
				Set entries = formsMap.entrySet();
				switch (userId.getRole()) {
				case SADM:
					for (Iterator iterator = entries.iterator(); iterator
							.hasNext();) {
						Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator
								.next();
						if (entry.getValue() != null) {
							geoForms.add(entry.getKey());
						}
					}
					break;
				default:
					for (Iterator iterator = entries.iterator(); iterator
							.hasNext();) {
						Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator
								.next();
						if (entry.getValue() != null
								&& entry.getValue().toLowerCase().contains(userId.getGeo().toLowerCase())) {
							geoForms.add(entry.getKey());
						}
					}
					break;
				}
				responseMap.put("geoforms", geoForms);
				serveResponse(response, JSONUtils.generateJSON(responseMap));
			} else if (path.equals("/bin/fp/survey/create")) {
				SurveyDTO surveyDto = SurveyRequestParamsHandler
						.processParameters(request, userId);
				boolean status = surveyor.createSurvey(surveyDto);
				Map statusMap = new HashMap();
				statusMap.put("status", status);
				serveResponse(response, JSONUtils.generateJSON(statusMap));
			} else if (path.equals("/bin/fp/survey/list")) {
				List data = surveyor.listSurveys(userId);
				serveResponse(response, JSONUtils.generateJSON("surveys",data));
			} else if (path.equals("/bin/fp/survey/getData")) {
				Map params = request.getParameterMap();
				String id = ((String[]) params.get(SharedConstants.SURVEY_ID))[0];
				SurveyDTO survey = surveyor.getSurvey(id);
				serveResponse(response, JSONUtils.generateJSON("survey",Arrays.asList(survey)));
			}else if (path.equals("/bin/fp/survey/update")) {
				Map params = request.getParameterMap();
				String id = ((String[]) params.get(SharedConstants.SURVEY_ID))[0];
				boolean updated = surveyor.updateSurvey(id,null);
				responseMap.put("status", updated);
				serveResponse(response, JSONUtils.generateJSON(responseMap));
			} else if (path.equals("/bin/fp/survey/delete")) {
				Map params = request.getParameterMap();
				String id = ((String[]) params.get(SharedConstants.SURVEY_ID))[0];
				boolean deleted = surveyor.deleteSurvey(id);
				responseMap.put("status", deleted);
				serveResponse(response, JSONUtils.generateJSON(responseMap));
			} 
		} catch (Exception e) {
			logger.error("Error exception; caused by: ", e);
		}

	}

}
