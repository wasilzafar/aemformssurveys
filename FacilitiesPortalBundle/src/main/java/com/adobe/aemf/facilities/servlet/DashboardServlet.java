package com.adobe.aemf.facilities.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.util.JSONUtils;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.dashboard.DashboardManager;
import com.adobe.aemf.facilities.um.UserIdentity;

@SlingServlet(paths = {"/bin/fp/dbdfetcher/submit"}, methods = "GET,POST")
@Service(Servlet.class)
public class DashboardServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;
	Logger logger = LoggerFactory.getLogger(DashboardServlet.class);

	@Reference
	private DashboardManager dashboardManager;	
	
	@Activate
	@Modified
	protected void activate() {}

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

	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> outMap = null;
		HttpSession session = request.getSession(false);
		UserIdentity userId = (UserIdentity) session.getAttribute(SharedConstants.SESSION_USER_ATTRIBUTE);
		String path = request.getRequestURI();
		try {
			if (path.equals("/bin/fp/dbdfetcher/submit")) {
				outMap = dashboardManager.getDashboardData(request,userId);
				responseMap.put("allformparameters", outMap);
				serveResponse(response,  JSONUtils.generateJSON(responseMap));
			}
		} catch (JSONException e) {
			logger.error("Error exception; caused by: ", e);
		}
	}

	private void serveResponse(SlingHttpServletResponse response, JSONObject outJSON) throws JSONException, IOException {
		logger.debug("Returned JSON response : " + outJSON);
		response.setContentType("application/json");
		outJSON.write(response.getWriter());
	}

}
