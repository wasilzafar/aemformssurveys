package com.adobe.aemf.facilities.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;

import com.day.cq.search.QueryBuilder;

public class RepositoryUtils {
	

public static final String SERVICERESOURCERESOLVER_SUBSERVICE = "facilitiesportaldashboard";

	/**
	 * Method to get {@link ResourceResolver}
	 * @param resolverFactory {@link ResourceResolverFactory}
	 * @param logger {@link Logger}
	 * @return {@link ResourceResolver}
	 * @throws LoginException 
	 */
	public static ResourceResolver getResourceResolver(ResourceResolverFactory resolverFactory) throws LoginException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE,SERVICERESOURCERESOLVER_SUBSERVICE);
		ResourceResolver resourceResolver = null;
		resourceResolver = resolverFactory.getServiceResourceResolver(param);
		return resourceResolver;
	}
	/**
	 * Method to get {@link QueryBuilder}
	 * @param resourceResolver {@link ResourceResolver}
	 * @return {@link QueryBuilder}
	 */
	public static QueryBuilder getQueryBuilder(ResourceResolver resourceResolver){
		QueryBuilder qb = null;
		qb = resourceResolver.adaptTo(QueryBuilder.class);
		return qb;
	}
	
	
	/**
	 * Method to get an instance of Jcr session {@link Session}
	 * @param resourceResolver {@link ResourceResolver}
	 * @return {@link Session}
	 */
	public static Session getJcrSession(ResourceResolver resourceResolver){
		Session session = null;
		session = resourceResolver.adaptTo(Session.class);
		return session;
	}
	
	/**
	 * Method to get an instance of Jcr session {@link Session}
	 * @param resourceResolver {@link ResourceResolver}
	 * @return {@link Node}
	 * @throws LoginException 
	 * @throws {@link RepositoryException} 
	 */
	public static Node getRootNode(ResourceResolverFactory resolverFactory) throws RepositoryException, LoginException{
		Node rootNode = null;
		ResourceResolver resourceResolver = RepositoryUtils.getResourceResolver(resolverFactory);
		Session session = resourceResolver.adaptTo(Session.class);
		rootNode = session.getRootNode();
		return rootNode;
	}
	
	public static Value createBinaryValue(Session session, String data) throws RepositoryException{
		ValueFactory factory = session.getValueFactory();
		InputStream is = new ByteArrayInputStream(data.getBytes());

		Binary binary = factory.createBinary(is);
		Value value = factory.createValue(binary);
		return value;
	}

}
