package com.adobe.aemf.facilities.scheduler;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value = Runnable.class)
//@Property( name = "scheduler.expression", value = "0 * * * * ?")
//@Property(name="scheduler.concurrent", boolValue=false)
//@Property( name = "scheduler.period", longValue = 10)
public class CriticalSubmissionCleaner implements Runnable{
	/** Default log. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /** The scheduler for rescheduling jobs. */
    @Reference
    private Scheduler scheduler;


    protected void activate(ComponentContext componentContext) throws Exception {
        //Scheduling job with schedule(Object job, ScheduleOptions options) method: executes the job every 5th second
        String schedulingExpression = "0 * * * * ?";
        scheduler.schedule(this, scheduler.EXPR(schedulingExpression));

    }

    protected void deactivate(ComponentContext componentContext) {
        log.info("Deactivated, goodbye!");
    }

	@Override
	public void run() {
		log.debug("Running ... CriticalSubmissionCleaner");
	}
}
