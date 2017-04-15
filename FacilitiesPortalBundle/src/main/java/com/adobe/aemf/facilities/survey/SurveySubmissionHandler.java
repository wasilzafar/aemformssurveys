package com.adobe.aemf.facilities.survey;

import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.PortalDataAccessManager;
import com.adobe.aemf.facilities.core.RepositoryUtils;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.exceptions.PortalException;
import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.asset.api.AssetMetadata;

@Component
@Service(value=SurveySubmissionHandler.class)
public class SurveySubmissionHandler {
	
	Logger logger = LoggerFactory.getLogger(SurveySubmissionHandler.class);
	
	@Reference
	PortalDataAccessManager pfm;
	
	@Reference
	ResourceResolverFactory resolverFactory;
	
	@Reference
	PortalConfigComponent portalConfig;
	
	private static final String SLING_FOLDER_NODETYPE = "sling:Folder";
	private static final boolean IS_DUPLICATE_SUBMISSION_ALLOWED = true;
	private static final String USER_GENERATED_CONTENT_FOLDER = "/content/usergenerated";

	
	public boolean handleSubmission(String formPath, Map dataMap, Session userSession) {
		boolean isSuccessful = false;
		logger.debug("Check values in handler : formPath: "+ formPath+"  dataMap:	"+ dataMap+" userId:	"+ userSession.getUserID());
		boolean isDuplicate = false;
		if(dataMap != null & userSession.getUserID() != null)
		try {
			String surveyId = (String) dataMap.get(SharedConstants.SURVEY_ID);
			String expectedSurveyStoragePathString = USER_GENERATED_CONTENT_FOLDER + formPath + "/" +surveyId+ "/" + userSession.getUserID();
			
			if (surveyId != null) {
				isDuplicate = isDuplicateSubmission(expectedSurveyStoragePathString);
				if(!IS_DUPLICATE_SUBMISSION_ALLOWED && !isDuplicate){
					saveSubmissionData(formPath, dataMap, userSession.getUserID());
				}else if(IS_DUPLICATE_SUBMISSION_ALLOWED){
					saveSubmissionData(formPath, dataMap, userSession.getUserID());
				}else{
					logger.error("Duplicate submission for survey ID : "+ surveyId + " not allowed.");
					throw new PortalException("Duplicate submission ! ", new Exception());
				}
				isSuccessful = true;
			}
		} catch (Exception e) {
			logger.error("Error while saving survey data : "+dataMap);		
		} 
		return isSuccessful;
	}

	
	private void saveSubmissionData(String formPath, Map dataMap, String userID) throws PortalException {
		boolean isPathValid = isPathValid(formPath);
		logger.debug("isPathValid : "+isPathValid);
		String surveyId = (String) dataMap.get(SharedConstants.SURVEY_ID);
		try {
			ResourceResolver resourceResolver = RepositoryUtils
					.getResourceResolver(resolverFactory);
			
/*			AssetManager afAM = resourceResolver.adaptTo(AssetManager.class);
			Asset afasset = afAM
					.getAsset("/content/dam/formsanddocuments/newtestaform");
			String afmd = afasset.getAssetMetadata().getXMP().dump();
			logger.debug("Granite Asset Metadata : " + afmd);
			com.day.cq.dam.api.Asset dayAsset = resourceResolver
					.getResource(
							"/content/dam/formsanddocuments/newtestaform")
					.adaptTo(com.day.cq.dam.api.Asset.class);
			logger.debug("Old Day Asset Metadata : " + dayAsset.getMetadata());*/
		Session session = resourceResolver.adaptTo(Session.class);
		Node root = RepositoryUtils.getRootNode(resolverFactory);
		
		if(isPathValid){
			String currentSurveyStoragePathString = USER_GENERATED_CONTENT_FOLDER + formPath + "/" +surveyId+ "/" + userID+ "_" + System.currentTimeMillis();
			Node targetNode;
			targetNode = JcrUtils.getOrCreateByPath(currentSurveyStoragePathString, SLING_FOLDER_NODETYPE, session);
			logger.debug("targetNode set at : "+targetNode.getPath());
			
			for (Iterator iterator = dataMap.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry entry = (Map.Entry) iterator.next();
				if(!((String)entry.getKey()).equalsIgnoreCase("jcr:data") && (entry.getValue() != null ||entry.getValue() != "null"))
					targetNode.setProperty((String)entry.getKey(), (String)entry.getValue(), PropertyType.STRING);
			}
			targetNode.setProperty(SharedConstants.IS_CRITICAL, "false", PropertyType.STRING);
			Node jcr_content = targetNode.addNode(Node.JCR_CONTENT, NodeType.NT_UNSTRUCTURED);
			String dataXML = (String) dataMap.get("jcr:data");
			logger.debug("Data XML to be stored : "+dataXML);
			jcr_content.setProperty("jcr:data", RepositoryUtils.createBinaryValue(session, dataXML));
			session.save();
			} 
		}catch (Exception e) {
			logger.error("Form is not located at correct location : "+e);
			throw new PortalException("Form is not located at correct location ", e);
		}
		
	}

	private boolean isPathValid(String formPath) {
		String formsRootPath = portalConfig.getFormsRootPath();
		if(formPath.contains(formsRootPath))
			return true;
		
		return false;
	}

	
	public boolean isDuplicateSubmission(String expectedSurveyStoragePathString) throws LoginException, RepositoryException {
		boolean exists = false;
		ResourceResolver resourceResolver = RepositoryUtils.getResourceResolver(resolverFactory);
		Session session = resourceResolver.adaptTo(Session.class);
		String deepestNode = expectedSurveyStoragePathString.substring(0,expectedSurveyStoragePathString.lastIndexOf("/"));
		if(session.nodeExists(deepestNode)){
			Node submitNode = session.getNode(deepestNode);
			for (Iterator iterator = submitNode.getNodes(); iterator.hasNext();) {
				Node childNode = (Node) iterator.next();
				logger.debug("Searching nodes in existing survey submmissions ? "+ childNode.getPath());
				if(childNode.getPath().contains(expectedSurveyStoragePathString.substring(expectedSurveyStoragePathString.lastIndexOf("/")+1))){
					exists = true;
				}
			}
		}
		logger.debug("Is duplicate submission ? "+ exists);
		return exists;
	}
}
