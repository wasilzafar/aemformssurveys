package com.adobe.aemf.facilities.scheduler;

import java.util.Date;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.SharedConstants;
import com.adobe.aemf.facilities.survey.SurveyDTO;
import com.adobe.aemf.facilities.survey.Surveyor;

@Component
@Service(value = Runnable.class)
//@Property( name = "scheduler.expression", value = "0/30 * * * * ?")
//@Property(name="scheduler.concurrent", boolValue=false)
//@Property( name = "scheduler.period", longValue = 10)
public class SurveyScheduler implements Runnable{

    Logger logger = LoggerFactory.getLogger(SurveyScheduler.class);

    @Reference
    private Scheduler scheduler;

	@Reference
	Surveyor surveyor;
	
    protected void activate(ComponentContext componentContext) throws Exception {
        String schedulingExpression = "0 * * * * ?";
        boolean added = scheduler.schedule(this, scheduler.EXPR(schedulingExpression));
        logger.debug("Is survey scheduler started ? "+ (added == true ? "Yes":"No"));
}

    protected void deactivate(ComponentContext componentContext) {
        logger.info("Deactivated, goodbye from SurveyScheduler!");
    }

	@Override
	public void run() {
		logger.debug("Checking active surveys ....      ");
		List<SurveyDTO> activeSurveys = getActiveSurveys();
		
		if (activeSurveys != null) {
			for (SurveyDTO surveyDTO : activeSurveys) {
				boolean isStatusValid = validateSurveyStatus(surveyDTO);
				logger.debug("Survey : " + surveyDTO.getSurveyId()+ " isStatusValid : " + isStatusValid);
				if (!isStatusValid) {
					updateSurveyStatus(surveyDTO);
					logger.info("Completed survey with ID "+surveyDTO.getSurveyId());

				}
			}
		}
		logger.debug("No Active surveys to be updated found !");
	}

	private void updateSurveyStatus(SurveyDTO surveyDTO) {
		surveyor.updateSurvey(surveyDTO.getSurveyId(), SharedConstants.SURVEY_STATUS_COMPLETE);
	}

	private boolean validateSurveyStatus(SurveyDTO surveyDTO) {
		String duration = surveyDTO.getDuration();
		Date creationDate = surveyDTO.getCreationDate();
		long currentTime = new Date().getTime();
		long surevyActiveDuration = getMilliSeconds(creationDate,duration);
		logger.debug("Current time < Survey activation duration ? :: "+currentTime+" : "+currentTime);
		return currentTime < surevyActiveDuration;
	}

	private long getMilliSeconds(Date creationDate, String duration) {
		long millisecondsCount = 0;
		String days = null;
		String hours = null;
		String minutes = null;
		String[] tokens = duration.split(":");
		if (tokens.length == 3) {
			days = tokens[0];
			hours = tokens[1];
			minutes = tokens[2];
		} 
		int daysInt = Integer.parseInt(days);
		int hoursInt = Integer.parseInt(hours);
		int minutesInt = Integer.parseInt(minutes);
		millisecondsCount = millisecondsCount + (daysInt*24 * 60 * 60 * 1000);
		millisecondsCount = millisecondsCount + (hoursInt * 60 * 60 * 1000);
		millisecondsCount = millisecondsCount + (minutesInt * 60 * 1000);
		
		return millisecondsCount + creationDate.getTime();
	}

	private synchronized List<SurveyDTO> getActiveSurveys() {
		List<SurveyDTO> activeSurveys = surveyor.listActiveSurveys();
		logger.debug("Active survey count : "+activeSurveys.size());
		return activeSurveys;
	}
}
