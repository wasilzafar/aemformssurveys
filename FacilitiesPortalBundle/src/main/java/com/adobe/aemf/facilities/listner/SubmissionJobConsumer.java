package com.adobe.aemf.facilities.listner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.NodeType;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.RepositoryUtils;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.util.XStreamHelper;

@Component(label = "Critical Submission Handler", metatype = true)
@Service(value = JobConsumer.class)
@Property(name = "job.topics", value = FormSubmissionSlingService.JOB_TOPIC)
public class SubmissionJobConsumer implements JobConsumer {
	private static final long serialVersionUID = 1L;

	Logger logger = LoggerFactory.getLogger(SubmissionJobConsumer.class);

	private String tempPath;

	@Reference
	PortalConfigComponent portalConfig;

	String[] critProps;

	@Activate
	@Modified
	protected void activate(ComponentContext ctx) {
		tempPath = portalConfig.getTempPath();
		critProps = portalConfig.getCritProps();
	}

	@Deactivate
	protected void deactivate(ComponentContext ctx) {

	}

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public JobResult process(Job job) {
		ResourceResolver resourceResolver = null;
		List allCriticalParams = Arrays.asList(critProps);
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			resourceResolver = RepositoryUtils.getResourceResolver(resolverFactory);
			Map dataMap = new HashMap();
			boolean toBeAdded = false;
			final String resourcePath = (String) job.getProperty(SharedConstants.DATA_NODE_ADDED);
			final Resource res = resourceResolver.getResource(resourcePath);
			Node subNode = res.adaptTo(Node.class);
			String surveyId = subNode.getProperty(SharedConstants.SURVEY_ID).getValue().getString();
			List<String> blackListed = (List<String>) Arrays.asList(portalConfig.getRestrictedProps());
			logger.debug("Form submitted by survey ID : "+surveyId);
			PropertyIterator pi = subNode.getProperties();
			for (Iterator<javax.jcr.Property> iterator = pi; iterator.hasNext();) {
				javax.jcr.Property prop = (javax.jcr.Property) iterator.next();
				dataMap.put(prop.getName(), getPropertyValue(prop));
				logger.debug("Iterating property : "+prop.getName()+" with value "+getPropertyValue(prop));
				if (containsCaseInsensitive(getPropertyValue(prop), allCriticalParams)) {
					logger.debug("Critical property found : "+prop.getName());					
					toBeAdded = true;
				}
			}
			if (toBeAdded) {
				logger.info("Node added at : " + res.getPath());
				Session session = resourceResolver.adaptTo(Session.class);
				Node node = JcrUtils.getNodeIfExists(res.getPath(), session);
				node.getProperty(SharedConstants.IS_CRITICAL).setValue("true");
				session.save();
			}
			return JobResult.OK;
		} catch (final Exception e) {
			logger.error("Exception: " + e, e);
			return JobResult.FAILED;
		} finally {
			resourceResolver.close();
		}
	}

	private String getPropertyValue(javax.jcr.Property prop) throws ValueFormatException, IllegalStateException, RepositoryException {
		StringBuffer value = new StringBuffer();
		 if(prop.isMultiple()) // This condition checks for properties whose type is String[](String array)  
	      {      
	           Value[] values = prop.getValues();  
	           for(Value val: values){  
	        	   value.append(val.getString()); // this will output the value in string format  
	      }  
	      }else if(!prop.getDefinition().isMultiple()){ 
	           value.append(prop.getValue().getString());  
	      }  
		return value.toString();
	}
	
	private Value createBinaryValue(Session session, String data) throws RepositoryException{
		ValueFactory factory = session.getValueFactory();
		InputStream is = new ByteArrayInputStream(data.getBytes());

		Binary binary = factory.createBinary(is);
		Value value = factory.createValue(binary);
		return value;
	}
	
	public boolean containsCaseInsensitive(String strToCompare, List<String>list)
	{
	    for(String str:list)
	    {
	        if(str.equalsIgnoreCase(strToCompare))
	        {
	            return(true);
	        }
	    }
	    return(false);
	}

}
