package com.adobe.aemf.facilities.search;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemds.guide.common.GuideContainer;
import com.adobe.aemds.guide.common.GuidePanel;
import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.PortalDataAccessManager;
import com.adobe.aemf.facilities.core.RepositoryUtils;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.survey.SurveyDTO;
import com.adobe.aemf.facilities.util.FPGuideUtils;
import com.adobe.aemf.facilities.util.JSONUtils;
import com.adobe.aemf.facilities.util.PathBuilder;
import com.day.cq.commons.date.InvalidDateException;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;
import com.day.jcr.vault.util.PathUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

@Component
@Service(value = SurveyDataSearch.class)
public class SurveyDataSearchImpl implements SurveyDataSearch {

	Logger logger = LoggerFactory.getLogger(SurveyDataSearchImpl.class);

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	PortalConfigComponent portalConfigComponent;
	
	@Reference
	SurveySearchHandler surveySearchHandler;
	
	@Reference
	private PortalDataAccessManager pFM;

	public Map getSurveyData(String id,SlingHttpServletRequest request) throws LoginException, RepositoryException, InvalidDateException {
        Map outMap = null;
		SurveyDTO surveyDTO = surveySearchHandler.getSurvetDTO(id);
		String formPath = pFM.getFormPath(surveyDTO.getForm());
		String dataPath = SharedConstants.USER_GENERATED_CONTENT_FOLDER+formPath+SharedConstants.PATH_SEPARATOR+id;
		logger.debug("Survey Id data storage path "+dataPath);		
		Session session =  RepositoryUtils.getJcrSession(RepositoryUtils.getResourceResolver(resolverFactory));
		if(session.nodeExists(dataPath)){
			outMap = getDataMapFromPath(formPath, id, request);	        
	        logger.debug("Transformed JSON to Map : "+outMap.toString());
		}
		return outMap;

	}

	private Map getDataMapFromPath(String formPath, String id,
			SlingHttpServletRequest request) throws LoginException {
		Map outMap = null;
		String dataPath = SharedConstants.USER_GENERATED_CONTENT_FOLDER
				+ formPath + SharedConstants.PATH_SEPARATOR + id;
		String formGCPath = formPath + SharedConstants.GUIDE_CONTAINER_SUFFIX;
		final StringWriter out = new StringWriter();
		final ResourceResolver resolver = RepositoryUtils
				.getResourceResolver(resolverFactory);
		Resource resource = resolver.getResource(formGCPath);
		GuideContainer gc = new GuideContainer(request, resource);
		GuidePanel guideRootPanel = gc.getRootPanel();
		Map<String, String> nameTitle = FPGuideUtils.allItemsNameTitleMap(guideRootPanel);
		StringBuffer outDataSB = null;
		Node node = resolver.getResource(dataPath).adaptTo(Node.class);
		JsonItemWriter jsonWriter = new JsonItemWriter(
				SharedConstants.PROPERTIESTOIGNORE);
		try {
			jsonWriter.dump(node, out, 1, true);
		} catch (RepositoryException | JSONException e) {
			logger.error("Could not get JSON", e);
		}
		outDataSB = out.getBuffer();
		
		logger.debug("Recieved output dump : " + outDataSB.toString());

		String outDataString = outDataSB.toString();

		for (Map.Entry<String, String> entry : nameTitle.entrySet()) {
			outDataString = outDataString.replace(entry.getKey(),entry.getValue());
		}
		logger.debug("Transformed string : " + outDataString);
		try {
			outMap = JSONUtils
					.jsonToMap(new org.json.JSONObject(outDataString));
		} catch (org.json.JSONException e) {
			logger.error("Exception while converting JSONObject to Map " + e);
		}
		return outMap;
	}
	public Map getSurveyDataStatistics(String id, SlingHttpServletRequest request) throws Exception {
		SurveyDTO surveyDTO = surveySearchHandler.getSurvetDTO(id);
		String formPath = pFM.getFormPath(surveyDTO.getForm());
		Map<String, Map<String, String>> dataMap = getDataMapFromPath(formPath, id, request);
		logger.debug("Raw data before statistics : "+dataMap);
		return doStatistics(dataMap);
	}
	
	
	Map doStatistics(Map<String,Map<String, String>> dataMap) throws RepositoryException{	
		Table table = HashBasedTable.create();
		/*
		 * In this part we are traversing all the user data nodes and storing the properties in a
		 * Multimap which allows duplicate values for a given key. The duplicate values are stored as ArrayList
		 */
		Multimap<String, String> multimap = ArrayListMultimap.create();
		for (Map.Entry<String, Map<String, String>> userData : dataMap.entrySet()) {
			Map<String, String> data = (Map<String, String>) userData.getValue();
			for (Map.Entry<String, String> property : data.entrySet()) {
				multimap.put(property.getKey(), property.getValue());
			
			}
		}
		
		/*
		 * Above Multimap has property name as key and all the values stored under this single key as ArrayList with  duplicate values
		 * We will count these values using Multiset - A collection that supports order-independent equality, like Set, 
		 * but may have duplicate elements. Add all the values for a property in Multiset, then you can get unique element set and their count.
		 * Finally we put the value in a map as (property name is key, and value corresponding to each key is map of propery value and its count)
		 * and return this map.
		 */
		
		for (Iterator iterator2 = multimap.keySet().iterator(); iterator2
				.hasNext();) {
			String propKey = (String) iterator2.next();
			Multiset mSet = HashMultiset.create();
			mSet.addAll(multimap.get(propKey));
			for (Iterator msItr = mSet.elementSet().iterator(); msItr
					.hasNext();) {
				String propertyValue = (String) msItr.next();
				table.put(propKey, propertyValue, mSet.count(propertyValue));
			}
		}
		logger.debug("Statistical data : "+table.rowMap());
		return table.rowMap();
	}
	
	public String getSumissionDataPath(String dataNodeName) throws LoginException, RepositoryException{
		Query query;
		Map<String, String> map = new HashMap<String, String>();
		map.put("nodename","*" + dataNodeName + "*");
	
		map.put("path", portalConfigComponent.getFormDataRootPath());
		map.put("type", "sling:Folder");
		ResourceResolver rr = RepositoryUtils.getResourceResolver(resolverFactory);
		Session session = RepositoryUtils.getJcrSession(rr);
		query = RepositoryUtils.getQueryBuilder(rr).createQuery(
				PredicateGroup.create(map), session);
		query.setStart(0);
		query.setHitsPerPage(0);
		Node foundNode = null;
		SearchResult result = query.getResult();
		logger.debug("Data submission path found :"+result.getTotalMatches());
		for (Iterator iterator = result.getNodes(); iterator.hasNext();) {
			foundNode = (Node) iterator.next();		
			logger.debug("Node path: "+foundNode.getPath());
		} 
		if (foundNode != null) {
			return foundNode.getPath();
		} else {
			return "";
		}
	}
	
	
}
