package com.adobe.aemf.facilities.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.RepositoryUtils;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.survey.SurveyDTO;
import com.adobe.aemf.facilities.um.UserIdentity;
import com.day.cq.commons.date.InvalidDateException;
import com.day.cq.commons.date.DateUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;

@Component
@Service(value=SurveySearchHandler.class)
public class SurveySearchHandler {
	Logger logger = LoggerFactory.getLogger(SurveySearchHandler.class);
	
	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	PortalConfigComponent portalConfigComponent;
	
	
	public SurveyDTO getSurvetDTO(String surveyId) throws LoginException, RepositoryException, InvalidDateException{
		String surveyStoragePath,surveyPath = null;
		SurveyDTO surveyDto = null;
		surveyStoragePath = portalConfigComponent.getSurveyDataRootPath();
		if(surveyStoragePath != null && surveyId != null){
		surveyPath = surveyStoragePath+SharedConstants.PATH_SEPARATOR+surveyId;
		}
		Session session = RepositoryUtils.getJcrSession(RepositoryUtils.getResourceResolver(resolverFactory));	
		boolean doesSurveyExists = session.nodeExists(surveyPath);
		if(doesSurveyExists){
			Node surveyNode = session.getNode(surveyPath);			
			surveyDto = new SurveyDTO();
			surveyDto.setSurveyId(surveyNode.getProperty(SharedConstants.SURVEY_ID).getString());
			surveyDto.setCreatedBy(surveyNode.getProperty(SharedConstants.SURVEY_CREATEDBY).getString());
			surveyDto.setDescription(surveyNode.getProperty(SharedConstants.SURVEY_DESCRIPTION).getString());
			surveyDto.setGeo(surveyNode.getProperty(SharedConstants.SURVEY_GEO).getString());
			surveyDto.setSubject(surveyNode.getProperty(SharedConstants.SURVEY_EMAIL_SUBJECT).getString());
			surveyDto.setMessage(surveyNode.getProperty(SharedConstants.SURVEY_EMAIL_MESSAGE).getString());
			surveyDto.setStatus(surveyNode.getProperty(SharedConstants.SURVEY_STATUS).getString());
			surveyDto.setForm(surveyNode.getProperty(SharedConstants.SURVEY_FORM).getString());
			surveyDto.setParticipants(extracrtValuesAsStringArray(surveyNode.getProperty(SharedConstants.SURVEY_PARTICIPANTS).getValues()));
			surveyDto.setDuration(surveyNode.getProperty(SharedConstants.SURVEY_DURATION).getString());
			Property propCreated = (Property) surveyNode.getProperty(SharedConstants.JCR_CREATED);
			Calendar dateCreated = DateUtil.parseISO8601(propCreated.getString());
			surveyDto.setCreationDate(dateCreated.getTime());
			
		}
		return surveyDto;		
	}
	
	public List<SurveyDTO> getSurveyDTO(UserIdentity userId) throws PathNotFoundException, RepositoryException, LoginException, InvalidDateException{
		Session session = RepositoryUtils.getJcrSession(RepositoryUtils.getResourceResolver(resolverFactory));	
		Node surveyDataNode = session.getNode(portalConfigComponent.getSurveyDataRootPath());
		List<SurveyDTO> surveyList = new ArrayList<SurveyDTO>();
		if(userId == null){
			for (NodeIterator iterator = surveyDataNode.getNodes(); iterator.hasNext();) {
				Node node = (Node) iterator.next();
				surveyList.add(createSurveyDTO(node));		
			}
		}else{
			Query query;
			Map<String, String> map = new HashMap<String, String>();
			map.put("property", SharedConstants.SURVEY_CREATEDBY);
			map.put("property.value", userId.getId());
		
			map.put("path", surveyDataNode.getPath());
			map.put("type", "sling:Folder");
			map.put("property.depth", "1");		
			map.put("p.offset", "0");
			map.put("p.limit", "0");
			ResourceResolver rr = RepositoryUtils.getResourceResolver(resolverFactory);
			session = RepositoryUtils.getJcrSession(rr);
			query = RepositoryUtils.getQueryBuilder(rr).createQuery(
					PredicateGroup.create(map), session);
			query.setStart(0);
			query.setHitsPerPage(0);
			SearchResult result = query.getResult();
			surveyList = handleContent(result);
		}
		return surveyList;		
	}
	
	List<SurveyDTO> handleContent(SearchResult result) throws RepositoryException, InvalidDateException{
		List<SurveyDTO> outList = new ArrayList<SurveyDTO>();
		for (Iterator<Node> iterator = result.getNodes(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			outList.add(createSurveyDTO(node));
		}
		return outList;
	}
	
	SurveyDTO createSurveyDTO(Node node) throws RepositoryException, InvalidDateException{
		SurveyDTO surveyDto = new SurveyDTO();
		surveyDto.setSurveyId(node.getProperty(SharedConstants.SURVEY_ID).getString());
		surveyDto.setCreatedBy(node.getProperty(SharedConstants.SURVEY_CREATEDBY).getString());
		surveyDto.setDescription(node.getProperty(SharedConstants.SURVEY_DESCRIPTION).getString());
		surveyDto.setGeo(node.getProperty(SharedConstants.SURVEY_GEO).getString());
		surveyDto.setSubject(node.getProperty(SharedConstants.SURVEY_EMAIL_SUBJECT).getString());
		surveyDto.setMessage(node.getProperty(SharedConstants.SURVEY_EMAIL_MESSAGE).getString());
		surveyDto.setStatus(node.getProperty(SharedConstants.SURVEY_STATUS).getString());
		surveyDto.setForm(node.getProperty(SharedConstants.SURVEY_FORM).getString());
		surveyDto.setParticipants(extracrtValuesAsStringArray(node.getProperty(SharedConstants.SURVEY_PARTICIPANTS).getValues()));
		surveyDto.setDuration(node.getProperty(SharedConstants.SURVEY_DURATION).getString());
		Property propCreated = (Property) node.getProperty(SharedConstants.JCR_CREATED);
		Calendar dateCreated = DateUtil.parseISO8601(propCreated
				.getString());
		surveyDto.setCreationDate(dateCreated.getTime());
		return surveyDto;
	}
	
	private String[] extracrtValuesAsStringArray(Value[] values) {
		String[] array = new String[values.length];
		int i = 0;
		for (Value value : values) {
			try {
				array[i] = value.getString();
			} catch (Exception e) {
				logger.error("Error while extracting array values !"+e);
			} 
			i++;
		}
		return array;
	}
	
}
