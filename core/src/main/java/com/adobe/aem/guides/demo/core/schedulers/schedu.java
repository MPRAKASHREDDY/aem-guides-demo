package com.adobe.aem.guides.demo.core.schedulers;

import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.aem.guides.demo.core.services.EmployeeReportServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component (service=Runnable.class, immediate = true)
public class schedu implements Runnable {

    private static final Logger LOG =
        LoggerFactory.getLogger(EmployeeReportServiceImpl.class);

    @Reference 
    private Scheduler scheduler;

    private static final String JOB_NAME = "Employee Prakash Scheduler";

    protected void activate() {
        scheduler.schedule(this, scheduler.EXPR("0 0/1 * * * ?")); // Run every minute
        LOG.info("Scheduler activated for job: {}", JOB_NAME);
    }
    @Override
    public void run() {
        LOG.info("Executing scheduled job: {}", JOB_NAME);
        
    }
}
