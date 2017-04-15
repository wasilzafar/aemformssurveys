package com.adobe.aemf.facilities.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.Session;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.exceptions.PortalException;
import com.adobe.aemf.facilities.survey.SurveyDTO;
import com.adobe.aemf.facilities.util.PortalUtils;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component
@Service(value=Mailer.class)
public class Mailer {

	Logger logger = LoggerFactory.getLogger(Mailer.class);
	
	@Reference
	PortalDataAccessManager pfm;
	
	@Reference
	MessageGatewayService messageGatewayService;

	@Reference
	PortalConfigComponent portalConfig;
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	MessageGateway<HtmlEmail> messageGateway;
	
	
	public boolean sendMail(SurveyDTO surveyDto){
		try {
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put("subject", surveyDto.getSubject());
		properties.put("message", surveyDto.getMessage());
		String url = createFormURL(surveyDto.getForm());
		properties.put("links",appendSurveyId(url, surveyDto));
		Resource templateRsrc = RepositoryUtils.getResourceResolver(resolverFactory).getResource(
				portalConfig.getEmailTemplatePath());
		logger.info("Finding template at : " + templateRsrc.getPath());
		if (templateRsrc.getChild("simple") != null) {
			templateRsrc = templateRsrc.getChild("simple");
		}
		if (templateRsrc == null) {
			throw new IllegalArgumentException("Missing template: "
					+ portalConfig.getEmailTemplatePath());
		}

		final MailTemplate mailTemplate = MailTemplate.create(templateRsrc
				.getPath(),
				templateRsrc.getResourceResolver().adaptTo(Session.class));
		
			HtmlEmail email;
				email = mailTemplate.getEmail(
						StrLookup.mapLookup(properties), HtmlEmail.class);
				email.setTLS(true);
				email.setSslSmtpPort("587");
			
			logger.debug("Adding recipients");
			String[] participants = surveyDto.getParticipants();
			for (String participant : participants) {
				email.addTo(participant);
			}

			this.messageGateway = this.messageGatewayService
					.getGateway(HtmlEmail.class);
			this.messageGateway.send(email);
			
		} catch (Exception e) {
				logger.error("Error while sending email; nested exception : "+e.getMessage()); 
				return false;
			} 

		return true;
		
	}
	
	private String createFormURL(String form) throws PortalException {
		String link = "";
		Map<String, String> mapFormsPath = new HashMap<String, String>();

		mapFormsPath = pfm.getAllForms();
		for (Iterator<Entry<String, String>> iterator = mapFormsPath.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String, String> formEntry = (Entry<String, String>) iterator
					.next();
			String path = formEntry.getValue();
			if (path.contains(form))
				link = new String(portalConfig.getBaseURL()
						+ path + ".html");
		}
		return link;
	}
	
	private String appendSurveyId(String formUrl,SurveyDTO surveyDto)  {
		String param = "";
		if (formUrl != null && formUrl != "") {
			 param = "?wcmmode=disabled&surveyId="+PortalUtils.encryptInt(surveyDto.getSurveyId());			
		}
		String url = formUrl+param;
		return new String("<a href='"+url+"'>" + surveyDto.getForm() + "</a>");
	}
}
