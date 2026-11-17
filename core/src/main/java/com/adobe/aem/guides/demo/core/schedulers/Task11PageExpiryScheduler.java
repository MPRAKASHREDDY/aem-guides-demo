package com.adobe.aem.guides.demo.core.schedulers;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = Task11PageExpiryScheduler.class,
    immediate = true
)
@Designate(ocd = Task11PageExpiryScheduler.Config.class)
public class Task11PageExpiryScheduler implements Runnable {

    private static final Logger LOG =
        LoggerFactory.getLogger(Task11PageExpiryScheduler.class);

    private static final String SCHEDULER_NAME =
        "task11-page-expiry-scheduler";

    private static final String EXPIRY_PROPERTY = "expiryDate";

    private static final String SERVICE_SUBSERVICE =
        "demo-publish-service";

    @ObjectClassDefinition(
        name = "Task-11 Page Expiry Scheduler",
        description = "Publishes and unpublishes pages based on expiryDate"
    )
    public @interface Config {

        @AttributeDefinition(
            name = "Cron Expression",
            description = "Cron expression for scheduler execution"
        )
        String cronExpression() default "0 0/3 * * * ?";

        @AttributeDefinition(
            name = "Content Root Path"
        )
        String rootPath() default "/content/demo";
    }

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private Replicator replicator;

    private String rootPath;

    private String cronExpression;

    @Activate
    @Modified
    protected void activate(Config config) {

        this.rootPath = config.rootPath();
        this.cronExpression = config.cronExpression();

        scheduler.unschedule(SCHEDULER_NAME);

        try {
            ScheduleOptions options =
                scheduler.EXPR(cronExpression);

            options.name(SCHEDULER_NAME);
            options.canRunConcurrently(false);

            boolean scheduled = scheduler.schedule(this, options);

            if (scheduled) {
                LOG.info(
                    "Task-11: Scheduler registered successfully. Cron: {}",
                    cronExpression
                );
            } else {
                LOG.error(
                    "Task-11: Scheduler registration failed. Cron: {}",
                    cronExpression
                );
            }

        } catch (Exception e) {
            LOG.error("Task-11: Unable to register scheduler", e);
        }
    }

    @Deactivate
    protected void deactivate() {

        scheduler.unschedule(SCHEDULER_NAME);

        LOG.info("Task-11: Scheduler stopped.");
    }

    @Override
    public void run() {

        LOG.info("Task-11: Scheduler execution started.");

        Map<String, Object> authInfo =
            Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,
                SERVICE_SUBSERVICE
            );

        try (ResourceResolver resolver =
                 resolverFactory.getServiceResourceResolver(authInfo)) {

            Session session = resolver.adaptTo(Session.class);

            if (session == null) {
                LOG.error(
                    "Task-11: Unable to obtain JCR session."
                );
                return;
            }

            Resource root = resolver.getResource(rootPath);

            if (root == null) {
                LOG.error(
                    "Task-11: Root path does not exist: {}",
                    rootPath
                );
                return;
            }

            long currentTime = System.currentTimeMillis();

            LOG.info(
                "Task-11: Checking pages under {} at {}",
                rootPath,
                Calendar.getInstance().getTime()
            );

            scanPages(root, resolver, session, currentTime);

            LOG.info("Task-11: Scheduler execution completed.");

        } catch (Exception e) {
            LOG.error(
                "Task-11: Scheduler execution failed.",
                e
            );
        }
    }

    private void scanPages(
        Resource resource,
        ResourceResolver resolver,
        Session session,
        long currentTime
    ) {

        if ("cq:Page".equals(
            resource.getValueMap().get(
                "jcr:primaryType", String.class
            )
        )) {

            processPage(
                resource,
                resolver,
                session,
                currentTime
            );
        }

        Iterator<Resource> children =
            resource.listChildren();

        while (children.hasNext()) {

            Resource child = children.next();

            // Do not scan page component content trees.
            if ("jcr:content".equals(child.getName())) {
                continue;
            }

            scanPages(
                child,
                resolver,
                session,
                currentTime
            );
        }
    }

    private void processPage(
        Resource page,
        ResourceResolver resolver,
        Session session,
        long currentTime
    ) {

        String pagePath = page.getPath();

        try {

            Resource content = resolver.getResource(
                pagePath + "/jcr:content"
            );

            if (content == null) {
                LOG.warn(
                    "Task-11: jcr:content missing for {}",
                    pagePath
                );
                return;
            }

            ValueMap properties = content.getValueMap();

            Calendar expiryDate =
                properties.get(EXPIRY_PROPERTY, Calendar.class);

            if (expiryDate == null) {

                LOG.debug(
                    "Task-11: expiryDate not found for {}. Skipping.",
                    pagePath
                );

                return;
            }

            boolean shouldBePublished =
                expiryDate.getTimeInMillis() >= currentTime;

            ReplicationStatus status =
                replicator.getReplicationStatus(session, pagePath);

            boolean isPublished =
                status != null && status.isActivated();

            LOG.info(
                "Task-11: Page: {}, Expiry: {}, Desired state: {}, Current state: {}",
                pagePath,
                expiryDate.getTime(),
                shouldBePublished ? "PUBLISHED" : "UNPUBLISHED",
                isPublished ? "PUBLISHED" : "UNPUBLISHED"
            );

            if (shouldBePublished && !isPublished) {

                LOG.info(
                    "Task-11: Publishing page: {}",
                    pagePath
                );

                replicator.replicate(
                    session,
                    ReplicationActionType.ACTIVATE,
                    pagePath
                );

                LOG.info(
                    "Task-11: Publish request submitted: {}",
                    pagePath
                );

            } else if (!shouldBePublished && isPublished) {

                LOG.info(
                    "Task-11: Unpublishing page: {}",
                    pagePath
                );

                replicator.replicate(
                    session,
                    ReplicationActionType.DEACTIVATE,
                    pagePath
                );

                LOG.info(
                    "Task-11: Unpublish request submitted: {}",
                    pagePath
                );

            } else {

                LOG.debug(
                    "Task-11: No replication required for {}",
                    pagePath
                );
            }

        } catch (ReplicationException e) {

            LOG.error(
                "Task-11: Replication failed for page: {}",
                pagePath,
                e
            );

        } catch (Exception e) {

            LOG.error(
                "Task-11: Error processing page: {}",
                pagePath,
                e
            );
        }
    }
}