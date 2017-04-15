package com.adobe.aemf.facilities.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Session;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;

import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.RepositoryUtils;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.um.UserIdentity;
import com.adobe.aemf.facilities.um.UserInfoProcessor;

@SlingFilter(label = "Facilities Portal Filter", 
description = "Facilities Portal Filter to Authenticate Requests",
generateComponent = true, // True, if you want to leverage activate deactivate 
generateService = true, 
order = 10000, // The smaller the number, the earlier in the Filter chain (can go negative);
scope = SlingFilterScope.REQUEST //REQUEST, INCLUDE, FORWARD, ERROR, COMPONENT (REQUEST, INCLUDE, COMPONENT)
)
public class FacilitiesPortalAuthenticationFilter implements Filter,HttpSessionListener {

	private static final Logger log = LoggerFactory
			.getLogger(FacilitiesPortalAuthenticationFilter.class.getName());

	@Reference
	PortalConfigComponent portalConfig;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Usually, do nothing
	}

	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (!(request instanceof SlingHttpServletRequest)
				|| !(response instanceof SlingHttpServletResponse)) {
			// Not a SlingHttpServletRequest/Response, so ignore.
			chain.doFilter(request, response); // This line would let you proceed to the rest of the filters.
			return;
		}

		SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
		SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
		Resource resource = slingRequest.getResource();

		if ((resource.getPath().startsWith("/content/GWSforms")
				|| resource.getPath().startsWith("/bin/fp")) && slingRequest != null) {

			if(!slingRequest.isRequestedSessionIdValid() || ((HttpServletRequest) request).getSession(false) != null) {
				String userId = getUser(resource);
				log.debug("Found user in filter : "+userId);
				if (isUserAuthorised(userId)) {
					log.debug("User authorised !");
					HttpSession session = slingRequest.getSession(true);
					session.setAttribute(
							SharedConstants.SESSION_USER_ATTRIBUTE,
							getPortalUserIdentityObject(userId, resolverFactory));
					log.debug("Created a session with ID "+session.getId());
				} else {
					((HttpServletResponse)response).sendError((HttpServletResponse.SC_FORBIDDEN), "User unauthorized. Please contact portal administrator.");
					return;
				}
			}
			// to proceed with the rest of the Filter chain
		}
			
		chain.doFilter(request, response);
		return;
	}


	private boolean isUserAuthorised(String userId) {
		log.debug("Checking whether user authorised : "+userId);
		List<String> authorisedUsers = Arrays.asList(portalUsers);
		for (String userString : authorisedUsers) {
			if(userString.contains(userId))
				return true;				
		}
		return false;
	}
	
	UserIdentity getPortalUserIdentityObject(String userId, ResourceResolverFactory resolverFactory){
		List<String> authorisedUsers = Arrays.asList(portalUsers);
		UserIdentity user = null;
		for (String userInfo : authorisedUsers) {
			if (userInfo.split(":")[0].contains(userId)) {
				try {
					user = UserInfoProcessor.process(userInfo, resolverFactory);
				} catch (Exception e) {
					log.error("Exception while getting user; exception"+e.getMessage()); 
				}
			} 
		}
		
		return user;
		
	}

	private String getUser(Resource resource) {
		Session adminSession = null;
		adminSession = RepositoryUtils.getJcrSession(resource.getResourceResolver());
		String userID = adminSession.getUserID();
		return userID;
	}

	@Override
	public void destroy() {
	}

	private String[] portalUsers;

	@Activate
	@Modified
	protected void activate(final ComponentContext componentContext)
			throws Exception {
		portalUsers = portalConfig.getPortalUsersOrGroups();
	}

	@Deactivate
	protected void deactivate(ComponentContext ctx) {
	}



	@Override
	public void sessionCreated(HttpSessionEvent hse) {	
		hse.getSession().setMaxInactiveInterval(60); //in seconds
	}



	@Override
	public void sessionDestroyed(HttpSessionEvent hse) {		
	}
}
