package com.adobe.aemf.facilities.survey;

import java.util.Date;

public class SurveyDTO {
	
	private String surveyId;
	private String form;
	private String description;
	private String createdBy;
	private String[] participants;
	private String subject;
	private String message;
	private String status;
	private String geo;
	private String duration;
	private String surveyURL;
	private Date creationDate;
	
	public String getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String[] getParticipants() {
		return participants;
	}
	public void setParticipants(String[] participants) {
		this.participants = participants;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getSurveyURL() {
		return surveyURL;
	}
	public void setSurveyURL(String surveyURL) {
		this.surveyURL = surveyURL;
	}
}
