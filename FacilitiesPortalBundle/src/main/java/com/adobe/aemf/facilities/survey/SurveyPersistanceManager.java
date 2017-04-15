package com.adobe.aemf.facilities.survey;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.PortalDataAccessManager;
import com.adobe.aemf.facilities.core.RepositoryUtils;
import com.adobe.aemf.facilities.core.SharedConstants;

@Component
@Service(value=SurveyPersistanceManager.class)
public class SurveyPersistanceManager {
	@Reference
	PortalDataAccessManager pfm;
	
	@Reference
	ResourceResolverFactory resolverFactory;
	
	@Reference
	PortalConfigComponent portalConfig;
	
	private static final String SLING_FOLDER_NODETYPE = "sling:Folder";

	synchronized boolean save(SurveyDTO survey) {
		try {
			ResourceResolver resourceResolver = RepositoryUtils.getResourceResolver(resolverFactory);
			Session session = RepositoryUtils.getJcrSession(resourceResolver);
			Node rootNode = session.getRootNode();
			Node surveyDataNode = rootNode.getNode(portalConfig.getSurveyDataRootPath().substring(1));
			if(true){
			Node newFolder = JcrUtils.getOrAddNode(surveyDataNode, survey.getSurveyId(), SLING_FOLDER_NODETYPE);
			newFolder.setProperty(SharedConstants.SURVEY_ID, survey.getSurveyId(), PropertyType.STRING);
			newFolder.setProperty(SharedConstants.SURVEY_DESCRIPTION, survey.getDescription(), PropertyType.STRING);
			newFolder.setProperty(SharedConstants.SURVEY_EMAIL_SUBJECT, survey.getSubject(), PropertyType.STRING);
			newFolder.setProperty(SharedConstants.SURVEY_EMAIL_MESSAGE, survey.getMessage(), PropertyType.STRING);
			newFolder.setProperty(SharedConstants.SURVEY_PARTICIPANTS, survey.getParticipants(), PropertyType.STRING);
			newFolder.setProperty(SharedConstants.SURVEY_FORM, survey.getForm(), PropertyType.STRING);
			newFolder.setProperty(SharedConstants.SURVEY_CREATEDBY, survey.getCreatedBy(), PropertyType.STRING);
			newFolder.setProperty(SharedConstants.SURVEY_GEO, survey.getGeo(), PropertyType.STRING);
			newFolder.setProperty(SharedConstants.SURVEY_DURATION, survey.getDuration(), PropertyType.STRING);
			newFolder.setProperty(SharedConstants.SURVEY_STATUS, survey.getStatus(), PropertyType.STRING);
			session.save();
			return true;
			}
		} catch (Exception e) {
			
		} 
		return false;
	}
	
	
	synchronized boolean delete(String surveyId) {
		try {
			ResourceResolver resourceResolver = RepositoryUtils
					.getResourceResolver(resolverFactory);
			Session session = RepositoryUtils.getJcrSession(resourceResolver);
			Node rootNode = session.getRootNode();
			Node surveyDataNode = rootNode.getNode(portalConfig.getSurveyDataRootPath().substring(1));
			if (surveyDataNode.hasNodes()) {
				Node surveyNode = surveyDataNode.getNode(surveyId);
				surveyNode.remove();
				session.save();
				return true;
			}
		} catch (Exception e) {
			
		}
		return false;
	}


	public boolean update(String surveyId, String setStatus) {
		try {
			ResourceResolver resourceResolver = RepositoryUtils
					.getResourceResolver(resolverFactory);
			Session session = RepositoryUtils.getJcrSession(resourceResolver);
			Node rootNode = session.getRootNode();
			Node surveyDataNode = rootNode.getNode(portalConfig.getSurveyDataRootPath().substring(1));
			if (surveyDataNode.hasNodes()) {
				Node surveyNode = surveyDataNode.getNode(surveyId);
				Property surveyStatus = surveyNode.getProperty(SharedConstants.SURVEY_STATUS);
				String status = surveyStatus.getValue().getString();
				if (setStatus == null) {
					if (status
							.equalsIgnoreCase(SharedConstants.SURVEY_STATUS_ACTIVE)) {
						surveyStatus
								.setValue(SharedConstants.SURVEY_STATUS_DEACTIVATED);
					} else if (status
							.equalsIgnoreCase(SharedConstants.SURVEY_STATUS_DEACTIVATED)) {
						surveyStatus
								.setValue(SharedConstants.SURVEY_STATUS_ACTIVE);
					}
				}else{
					surveyStatus
					.setValue(setStatus);
				}
				session.save();
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
}
