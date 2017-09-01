package com.pmprental.read;

import java.text.ParseException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Application Lifecycle Listener implementation class StartReadBufferPL
 *
 */
public class StartReadBufferPL implements ServletContextListener {
	private static Scheduler sched;
    /**
     * Default constructor. 
     */
    public StartReadBufferPL() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	SchedulerFactory sf=new StdSchedulerFactory();
  	    try {
			sched = sf.getScheduler();
			JobDetail jd=new JobDetail("jobPl","groupPl",ReadMaquinaPlJob.class);
			//CronTrigger ct=new CronTrigger("cronTriggerPl","groupPl","0 0 4,12,20 * * ?");
			CronTrigger ct=new CronTrigger("cronTriggerPl","groupPl","0 0 6,12 * * ?");
			//CronTrigger ct=new CronTrigger("cronTriggerPl","groupPl","0 33 11 * * ?");
			sched.scheduleJob(jd,ct);
			sched.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
    	try {
			sched.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
    }
    
   
	
}
