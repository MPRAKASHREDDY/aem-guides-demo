package com.adobe.aem.guides.demo.core.schedulers;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.demo.core.services.Task15UpdateService;

@Component(
    service = Runnable.class,
    immediate = true,
    property = {
        "scheduler.expression=0 0/5 * * * ?",
        "scheduler.concurrent:Boolean=false",
        "scheduler.name=Task15 Scheduler"
    }
)
public class Task15Scheduler implements Runnable {

    private static final Logger LOG =
            LoggerFactory.getLogger(Task15Scheduler.class);

    @Reference
    private Task15UpdateService updateService;

    @Override
    public void run() {

        LOG.info("Task15 Scheduler started");

        try {
            updateService.updatePageProperties();

            LOG.info(
                "Task15 Scheduler successfully updated page properties"
            );

        } catch (Exception e) {

            LOG.error(
                "Error while executing Task15 Scheduler",
                e
            );
        }
    }
}