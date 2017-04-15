package com.adobe.aemf.facilities.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.adobe.aemds.guide.common.GuideContainer;
import com.adobe.aemds.guide.common.GuidePanel;
import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.PortalDataAccessManager;
import com.adobe.aemf.facilities.core.RepositoryUtils;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.exceptions.PortalException;
import com.adobe.aemf.facilities.search.SearchItem;
import com.adobe.aemf.facilities.search.SearchItemResult;
import com.adobe.aemf.facilities.search.SearchItemType;
import com.adobe.aemf.facilities.search.SearchSpec;
import com.adobe.aemf.facilities.search.SurveyDataSearch;
import com.adobe.aemf.facilities.search.SearchItem.SearchScope;
import com.adobe.aemf.facilities.survey.SurveyDTO;
import com.adobe.aemf.facilities.survey.Surveyor;
import com.adobe.aemf.facilities.um.Role;
import com.adobe.aemf.facilities.um.UserIdentity;
import com.adobe.aemf.facilities.util.FPGuideUtils;

@Component
@Service(value=DashboardManager.class)
public class DashboardManager {

	
	@Reference
	PortalConfigComponent portalConfig;
	
	@Reference
	Surveyor surveyor;
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	private SurveyDataSearch surveyDataSearch;
	
	@Reference
	PortalDataAccessManager portalFormManager;

	public Map getDashboardData(SlingHttpServletRequest request, UserIdentity userId) {
		boolean carryOn = true;
		Role sessionUserRole = userId.getRole();
		String geo = userId.getGeo();
		String id = userId.getId();
		Map outMap =null;
		String surveyId = (String) request.getParameter(SharedConstants.SURVEY_ID);
		try {
			outMap= surveyDataSearch.getSurveyDataStatistics(surveyId, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outMap;
	}

}
