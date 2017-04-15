package com.adobe.aemf.facilities.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.exceptions.PortalException;

@Component(enabled=true,immediate=true)
@Service(value=PortalDataAccessManager.class)
public class JCRPortalDataAccessManagerImpl implements PortalDataAccessManager {
	Logger logger = LoggerFactory.getLogger(JCRPortalDataAccessManagerImpl.class);
	
	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private PortalConfigComponent portalConfig;
	
	public Map<String, String> getAllForms() {
		String formsRootPath = portalConfig.getFormsRootPath();
		Map<String, String> formsMap = new HashMap<String, String>();
		try {
			Node rNode = RepositoryUtils.getRootNode(resolverFactory);
			String childName = formsRootPath
					.substring(formsRootPath.indexOf("/") + 1);
			Node formsRootNode = rNode.getNode(childName);
			logger.debug("Forms path retieved : " + childName);
			java.lang.Iterable<Node> formsNode = JcrUtils
					.getChildNodes(formsRootNode);
			Iterator<Node> it = formsNode.iterator();

			if (it.hasNext()) {
				while (it.hasNext()) {
					Node node = (Node) it.next();
					java.lang.Iterable<Node> nodeChilds = JcrUtils
							.getChildNodes(node);
					Iterator<Node> childIt = formsNode.iterator();
					for (Node node2 : nodeChilds) {
						String name = node2.getName();
						logger.debug("Form found at path " + node2.getPath());
						formsMap.put(name, node2.getPath());
					}
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return formsMap;
	}

	@Override
	public List<String> getGeos() throws PortalException {
		String formsRootPath = portalConfig.getFormsRootPath();
		List<String> geos = new ArrayList<String>();
		try {
			if (formsRootPath != null) {
				Node rNode = RepositoryUtils.getRootNode(resolverFactory);
				Node allFormsRootNode = rNode.getNode(formsRootPath
						.substring(1));
				Iterable<Node> itrbl = JcrUtils.getChildNodes(allFormsRootNode);
				for (Iterator<Node> iterator = itrbl.iterator(); iterator
						.hasNext();) {
					Node geoNode = (Node) iterator.next();
					geos.add(geoNode.getName());

				}
			}
		} catch (Exception e) {
			throw new PortalException(
					"Error while retrieving form geos, nested exception", e);
		}
		return geos;
	}

	@Override
	public List<String> getFormMetadata(String formName) {
		return null;
	}
	
	@Override
	public String getFormPath(String formName){
		return getAllForms().get(formName);
	}

}
