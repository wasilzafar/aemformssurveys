package com.adobe.aemf.facilities.core;

import java.util.Dictionary;
import java.util.Observable;
import java.util.Set;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.oak.osgi.ObserverTracker;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.listner.FormSubmissionSlingService;

@Component(metatype = true, label = "Facilities Portal Configuration Component",immediate=true)
@Service(value = PortalConfigComponent.class)
public class PortalConfigComponent extends Observable {

	@Property(label = "Data Path", description = "Path to fetch all the facilities forms data", value = "/content/usergenerated/content/forms/af/Facilities-Portal")
	private static final String FORM_SUBMIT_DATA_PATH = "formsDataPath";

	@Property(label = "All Forms Path", description = "Path where all facilities forms exist", value = "/content/forms/af/Facilities-Portal")
	private static final String FORM_ROOT_PATH = "formsPath";
	
	@Property(label = "Survey Store Root Path", description = "Path where all facilities surveys will be stored", value = "/content/usergenerated/srvfacilities")
	private static final String SURVEY_DATA_ROOT_PATH = "surveyDataPath";

	@Property(label = "Facilities Users Or Groups", name = "portalUsers", 
			description = "User accessing portal - The user is specified in the format <User ID>:<Role>:<Geography>"
					+ "User ID is the login ID of the user\n"
					+ "Role - One of { UADM, GADM, SADM } . Stands for User admin, Geo admin and Super admin resp. \n"
					+ "Geography - One of { INDIA, AMERICA, GLOBAL }", value = { "admin:SADM:INDIA" }, unbounded=PropertyUnbounded.ARRAY)
	private static Set<String> portalUsers;
	public static final String PORTAL_USERS = "";

	@Property(label = "Base URL", name = "baseURL", description = "Base URL to create form links", value = "http://localhost:4502")
	private String baseURL;

	@Property(label = "Email template path", name = "emailTemplatePath", description = "Templates path used for sending form links in emails", value = "/etc/designs/portal/etemplates")
	private String emailTemplatePath;

	@Property(label = "Watch Path", name = "watchPath", description = "Path to watch for node addition", value = "/content/usergenerated/content/forms/af/Facilities-Portal")
	private String watchPath;

	@Property(label = "Temp storage path", name = "tempPath", description = "Temporary path for critical form submission storage", value = "/tmp/facilitiesportal")
	private String tempPath;

	@Property(name = "critProps", label = "Critical Properties", description = "Properties values for critical submission", value = { "Poor","No","Dissatisfied" })
	String[] critProps;

	@Property(name = "restrictedProps", label = "Blacklisted Properties", description = "Properties not to be included in forms data analysis", value = {
			"jcr:created", "runtimeLocale", "jcr:data", "jcr:createdBy",
			"guideContainerPath", "jcr:mixinTypes", "jcr:primaryType",
			"afSubmissionInfo", "comment", "name", "place", "email",
			"DateOfSubmission", "fileAttachmentMap", "surveyId" }, unbounded=PropertyUnbounded.ARRAY)
	private static Set<String> restrictedProperties;

	private String formDataRootPath;
	private String formsRootPath;
	private String surveyDataRootPath;
	private String[] portalUsersOrGroups;
	private String[] restrictedProps;

	@Activate
	@Modified
	protected void activate(ComponentContext ctx) {
		Dictionary<?, ?> props = ctx.getProperties();
		formDataRootPath = PropertiesUtil.toString(
				props.get("formsDataPath"),
				"/content/usergenerated/content/forms/af/Facilities-Portal");
		formsRootPath = PropertiesUtil.toString(props.get("formsPath"),
				"/content/forms/af/Facilities-Portal");
		surveyDataRootPath = PropertiesUtil.toString(props.get("surveyDataPath"),
				"/content/usergenerated/srvfacilities");
		critProps = PropertiesUtil.toStringArray(props.get("critProps"));
		baseURL = PropertiesUtil.toString(props.get("baseURL"),
				"http://localhost:4502");
		watchPath = PropertiesUtil.toString(props.get("watchPath"),
				"/content/usergenerated/content/forms/af/Facilities-Portal");
		emailTemplatePath = PropertiesUtil.toString(
				props.get("emailTemplatePath"),
				"/etc/designs/portal/etemplates");
		tempPath = PropertiesUtil.toString(props.get("tempPath"),
				"/tmp/facilitiesportal");
		portalUsersOrGroups = PropertiesUtil.toStringArray(props
				.get("portalUsers"));
		restrictedProps = PropertiesUtil.toStringArray(props.get("restrictedProps"));
	}

	public String[] getRestrictedProps() {
		return restrictedProps;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public String getEmailTemplatePath() {
		return emailTemplatePath;
	}

	public String getWatchPath() {
		return watchPath;
	}

	public String getTempPath() {
		return tempPath;
	}

	public String[] getCritProps() {
		return critProps;
	}

	@Deactivate
	protected void deactivate(ComponentContext ctx) {

	}

	public String getFormDataRootPath() {
		return formDataRootPath;
	}

	public String getFormsRootPath() {
		return formsRootPath;
	}
	
	public String getSurveyDataRootPath() {
		return surveyDataRootPath;
	}
	
	public String[] getPortalUsersOrGroups() {
		return portalUsersOrGroups;
	}
	
}
