package com.adobe.aemf.facilities.survey;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.RepositoryUtils;
import com.adobe.aemf.facilities.core.SharedConstants;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;

@Component
@Service(value=SurveyIdGenerator.class)
public class SurveyIdGenerator {
	Logger logger = LoggerFactory.getLogger(SurveyIdGenerator.class);
	private static SurveyIdGenerator INSTANCE;
	private AtomicInteger previousCount;

	@Reference
	ResourceResolverFactory resolverFactory;
	
	@Reference
	PortalConfigComponent portalConfigComponent;
	
	@Activate
	@Modified
	private void sync() {
		String previousIdString = null;
		int previousIdInt = 0;
		try {
			ResourceResolver rr = RepositoryUtils
					.getResourceResolver(resolverFactory);
			QueryBuilder qB = RepositoryUtils.getQueryBuilder(rr);
			Map<String, String> map = new HashMap<String, String>();
			map.put("path", portalConfigComponent.getSurveyDataRootPath());
			map.put("type", "sling:Folder");
			map.put(Predicate.ORDER_BY,"@jcr:created");
			map.put(Predicate.PARAM_SORT, Predicate.SORT_DESCENDING);
			map.put("p.limit", "1");

			Session session = RepositoryUtils.getJcrSession(rr);
			Query query = RepositoryUtils.getQueryBuilder(rr).createQuery(
					PredicateGroup.create(map), session);
			query.setStart(0);
			query.setHitsPerPage(0);
			SearchResult result = query.getResult();
			for (Iterator resNodeItr = result.getNodes(); resNodeItr.hasNext();) {
				Node node = (Node) resNodeItr.next();
				previousIdString = node.getProperty(SharedConstants.SURVEY_ID)
						.getString();
			}
			previousIdInt = Integer.parseInt(previousIdString);
			logger.debug("Last id found : "+previousIdInt);
			logger.info("YYYYYYYY        YYYYYYYY    EEEEEEEEEEEEEEEEEE      SSSSSSSSSSS  ");
			logger.info("YYYYYYYY        YYYYYYYY    EEEEEEEEEEEEEEEEEE     SSSSSSSSSSSSS ");
			logger.info("YYYYYYYY        YYYYYYYY    EEEEEEEEEEEEEEEEEE    SSSSSSSSSSSSSSS");
			logger.info("YYYYYYYY        YYYYYYYY    EEEEEEEEEEEEEEEEEE    SSSSSSSS    SSS");
			logger.info(" YYYYYYYY      YYYYYYYY     EEEEEEEE              SSSSSSSS       ");
			logger.info("  YYYYYYYY    YYYYYYYY      EEEEEEEE              SSSSSSSS       ");
			logger.info("   YYYYYYYY  YYYYYYYY       EEEEEEEEEEEEE          SSSSSSSS      ");
			logger.info("    YYYYYYYYYYYYYYYY        EEEEEEEEEEEEE           SSSSSSSSSS   ");
			logger.info("     YYYYYYYYYYYYYY         EEEEEEEEEEEEE            SSSSSSSSSS  ");
			logger.info("      YYYYYYYYYYYY          EEEEEEEEEEEEE               SSSSSSSSS");
			logger.info("       YYYYYYYYYY           EEEEEEEE                     SSSSSSSS");
			logger.info("        YYYYYYYY            EEEEEEEE                     SSSSSSSS");
			logger.info("        YYYYYYYY            EEEEEEEEEEEEEEEEE     SSS    SSSSSSSS");
			logger.info("        YYYYYYYY            EEEEEEEEEEEEEEEEE     SSSSSSSSSSSSSSS");
			logger.info("        YYYYYYYY            EEEEEEEEEEEEEEEEE      SSSSSSSSSSSSS ");
			logger.info("        YYYYYYYY            EEEEEEEEEEEEEEEEE       SSSSSSSSSSS  ");
		} catch (Exception e) {
			logger.debug("Unable to set last id : "+previousIdString);
		}
		previousCount = new AtomicInteger(previousIdInt);
	}

	public synchronized static SurveyIdGenerator getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SurveyIdGenerator();
		}
		return INSTANCE;
	}

	public synchronized String createID() {
		return String.format("%09d", previousCount.incrementAndGet());
	}

	public AtomicInteger getPreviousCount() {
		return previousCount;
	}
}
