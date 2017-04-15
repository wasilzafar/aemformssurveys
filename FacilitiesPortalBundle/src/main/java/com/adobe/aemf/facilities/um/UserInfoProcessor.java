package com.adobe.aemf.facilities.um;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.RepositoryUtils;

public class UserInfoProcessor {

	private static final String USER_INFO_SEPARATOR = ":";
	static Logger logger = LoggerFactory.getLogger(UserInfoProcessor.class);

	public static UserIdentity process(String userInfo,
			ResourceResolverFactory resolverFactory)
			throws RepositoryException, LoginException {
		UserIdentity uId = null;
		if (userInfo != null) {
			String[] tokens = userInfo.split(USER_INFO_SEPARATOR);
			String id = tokens[0];
			String role = tokens[1];
			String geo = tokens[2];

			ResourceResolver rr = RepositoryUtils
					.getResourceResolver(resolverFactory);
			UserManager um = rr.adaptTo(UserManager.class);
			Authorizable authorizable = um.getAuthorizable(id);

			String lastName = authorizable.getProperty("./profile/familyName") != null ? authorizable
					.getProperty("./profile/familyName")[0].getString() : null;
			String firstName = authorizable.getProperty("./profile/givenName") != null ? authorizable
					.getProperty("./profile/givenName")[0].getString() : null;
			String userId = authorizable.getID() != null ? authorizable.getID()
					: null;
			String fullName;
			if (firstName != null && lastName != null) {
				fullName = firstName + " " + lastName;
			} else if (firstName != null && lastName == null) {
				fullName = firstName;
			} else if (firstName == null && lastName != null) {
				fullName = lastName;
			} else {
				fullName = userId;
			}
			uId = new UserIdentity();
			uId.setId(id);
			uId.setFullName(fullName);
			if (geo != null) {
				uId.setGeo(geo);
			}
			if (role != null) {
				for (Role roleValue : Role.values()) {
					if (roleValue.name().equalsIgnoreCase(role)) {
						uId.setRole(roleValue);
					}
				}
			}
			String emailID = null;
			emailID = authorizable.getProperty("./profile/email") != null ? authorizable
					.getProperty("./profile/email")[0].getString() : "";

			uId.setEmail(emailID);

		}
		return uId;
	}
}
