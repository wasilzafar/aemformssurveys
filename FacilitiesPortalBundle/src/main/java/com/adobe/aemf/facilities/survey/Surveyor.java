package com.adobe.aemf.facilities.survey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.Mailer;
import com.adobe.aemf.facilities.core.PortalDataAccessManager;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.exceptions.PortalException;
import com.adobe.aemf.facilities.search.SearchItem;
import com.adobe.aemf.facilities.search.SearchItemResult;
import com.adobe.aemf.facilities.search.SearchItemType;
import com.adobe.aemf.facilities.search.SearchSpec;
import com.adobe.aemf.facilities.search.SearchItem.SearchScope;
import com.adobe.aemf.facilities.search.SurveySearchHandler;
import com.adobe.aemf.facilities.um.Role;
import com.adobe.aemf.facilities.um.UserIdentity;
import com.day.cq.commons.date.InvalidDateException;

@Component
@Service(value=Surveyor.class)
public class Surveyor {
	
	Logger logger = LoggerFactory.getLogger(Surveyor.class);	

	@Reference
	PortalDataAccessManager pfm;

	@Reference
	SurveyPersistanceManager surveyPersistanceManager;
	
	@Reference
	Mailer mailer;
	
	@Reference
	SurveySearchHandler surveySearchHandler;
	
	public List< SurveyDTO> listSurveys(UserIdentity userId) {
		List< SurveyDTO> surveys = null;
		try {
			surveys = surveySearchHandler.getSurveyDTO(userId);
		} catch (RepositoryException | LoginException | InvalidDateException e) {
			logger.error("Error wile   fetching surveys for user: "+userId.getId());
		}
		return surveys;
	}
	
	public boolean isSurveyActive(String surveyId){
		SurveyDTO survey = null;
		try {
			survey = surveySearchHandler.getSurvetDTO(surveyId);
		} catch (LoginException | RepositoryException | InvalidDateException e) {
			logger.error("Error while checking survey status for ID: "+surveyId);
		}
		
		return (SharedConstants.SURVEY_STATUS_ACTIVE.equalsIgnoreCase(survey.getStatus()));
		
	}
	
	public boolean createSurvey(final SurveyDTO surveyDto) {
		surveyPersistanceManager.save(surveyDto);
		new Thread() {
			public void run() {
				mailer.sendMail(surveyDto);
			};
		}.start();
		return true;
	}
	
	public boolean deleteSurvey(String surveyId){
		boolean isDeleted = false;
		isDeleted = surveyPersistanceManager.delete(surveyId);
		return isDeleted;
	}
	
	public boolean updateSurvey(String surveyId, String status){
		boolean isUpdated = false;
		isUpdated = surveyPersistanceManager.update(surveyId,status);
		return isUpdated;
	}
	
	public SurveyDTO getSurvey(String surveyId) {
		SurveyDTO survey = null;
		try {
			survey = surveySearchHandler.getSurvetDTO(surveyId);
		} catch (LoginException | RepositoryException | InvalidDateException e) {
			logger.error("Error while fetching survey for ID: " + surveyId);
		}
		return survey;
	}

	public List<SurveyDTO> listActiveSurveys() {
		List< SurveyDTO> activeSurveys = null;
		List< SurveyDTO> surveys = null;
		UserIdentity userId = null;
		try {
			surveys = surveySearchHandler.getSurveyDTO(userId);
		} catch (RepositoryException | LoginException | InvalidDateException e) {
			logger.error("Error while fetching surveys for user: "+userId.getId());
		}
		
		if (surveys != null) {
			activeSurveys = new ArrayList< SurveyDTO>();
			for (Iterator<SurveyDTO> iterator = surveys.iterator(); iterator
					.hasNext();) {
				SurveyDTO surveyDTO = iterator.next();
				if (surveyDTO.getStatus().equalsIgnoreCase(
						SharedConstants.SURVEY_STATUS_ACTIVE)) {
					activeSurveys.add(surveyDTO);
				}

			}
		}
		return activeSurveys;
	}
}
