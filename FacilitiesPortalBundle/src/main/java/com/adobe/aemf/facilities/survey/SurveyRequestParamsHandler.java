package com.adobe.aemf.facilities.survey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sling.api.SlingHttpServletRequest;

import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.um.UserIdentity;

public class SurveyRequestParamsHandler {

	public static SurveyDTO processParameters(SlingHttpServletRequest request, UserIdentity userId) {
		SurveyDTO surveyDTO = new SurveyDTO();
		Map params = request.getParameterMap();
		String id = ((String[]) params.get(SharedConstants.SURVEY_ID))[0];
		surveyDTO.setSurveyId(id);
		String desc = ((String[]) params.get(SharedConstants.SURVEY_DESCRIPTION))[0];
		surveyDTO.setDescription(desc);
		surveyDTO.setGeo(userId.getGeo());
		surveyDTO.setStatus(SharedConstants.SURVEY_STATUS_ACTIVE);
		String duration = ((String[]) params.get(SharedConstants.SURVEY_DURATION))[0];
		surveyDTO.setDuration(duration);
		String email_subject = ((String[]) params.get(SharedConstants.SURVEY_EMAIL_SUBJECT))[0];
		surveyDTO.setSubject(email_subject);
		String email_message = ((String[]) params.get(SharedConstants.SURVEY_EMAIL_MESSAGE))[0];
		surveyDTO.setMessage(email_message);
		List<String> participants = findEmails(((String[]) params.get(SharedConstants.SURVEY_PARTICIPANTS))[0]);
		surveyDTO.setParticipants(Arrays.copyOf(participants.toArray(), participants.toArray().length, String[].class));
		String forms = ((String[]) params.get(SharedConstants.SURVEY_FORM))[0];
		surveyDTO.setForm(forms);
		surveyDTO.setCreatedBy(userId.getId());
		return surveyDTO;
	}
	
	private static List findEmails(String s) {
        List<String> allEmail = new ArrayList<String>();
        Pattern p = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b",Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        while (m.find()){
        	String matched = m.group();
        	allEmail.add(matched);
        }
		return allEmail;
    }
}
