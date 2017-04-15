package com.adobe.aemf.facilities.listner;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.jcr.version.VersionException;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.oak.spi.security.principal.EveryonePrincipal;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.RepositoryUtils;
import com.adobe.aemf.facilities.core.SharedConstants;


@Component(immediate = true)
@Service(value = EventHandler.class)
@Property(name = org.osgi.service.event.EventConstants.EVENT_TOPIC, value = org.apache.sling.api.SlingConstants.TOPIC_RESOURCE_ADDED)
public class FormSubmissionSlingService implements EventHandler {

	Logger logger = LoggerFactory.getLogger(FormSubmissionSlingService.class);

	private String watchPath;
	
	@Reference
	private JobManager jobManager;

	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	PortalConfigComponent portalConfig;
	
	@Activate
	@Modified
	protected void activate(ComponentContext ctx)
	  {
	    watchPath = portalConfig.getWatchPath();
	  }
	  
	@Deactivate
	  protected void deactivate(ComponentContext ctx)
	  {

	  }
	
	/** The job topic for submit job events. */
	public static final String JOB_TOPIC = "com/adobe/aemf/facilitiesportal/submission";

	@Override
	public void handleEvent(Event event) {
		logger.debug("Handling event : "+ ((String) event.getProperty(SlingConstants.PROPERTY_PATH)));

		// get the resource event information
		final String propPath = (String) event.getProperty(SlingConstants.PROPERTY_PATH);
		final String propResType = (String) event.getProperty(SlingConstants.PROPERTY_RESOURCE_TYPE);
		int pathSeparatorCount = StringUtils.countMatches(propPath, "/");
		
		// a job is started if a node is added to watchPath
		if (propPath.startsWith(watchPath) && "sling:Folder".equals(propResType)
				&& (pathSeparatorCount == 10)) {
			// create payload
			final Map<String, Object> payload = new HashMap<String, Object>();
			payload.put(SharedConstants.DATA_NODE_ADDED, propPath);
			// start job

			this.jobManager.addJob(JOB_TOPIC, payload);
			logger.info("Submission job has been started for: {}", propPath);
		}
	}
	

}
