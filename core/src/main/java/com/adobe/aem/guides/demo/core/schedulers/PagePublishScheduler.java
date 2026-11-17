
package com.adobe.aem.guides.demo.core.schedulers;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.commons.scheduler.ScheduleOptions;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.AttributeDefinition;

import com.day.cq.replication.Replicator;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = PagePublishScheduler.Config.class)

public class PagePublishScheduler implements Runnable {

    private static final Logger LOG =
            LoggerFactory.getLogger(PagePublishScheduler.class);

    private static final String JOB_NAME = "page-publish-scheduler";
    private static final String SUBSERVICE = "page-publisher";

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicator;

    private String cronExpression;
    private String rootPagePath;

    @ObjectClassDefinition(name = "Demo Page Publish Scheduler")
    public @interface Config {

        @AttributeDefinition(name = "Cron Expression")
        String cronExpression() default "0 0/5 * * * ?";

        @AttributeDefinition(name = "Root Page Path")
        String rootPagePath() default "/content/demo/us/en";
    }

    @Activate
    @Modified
    protected void activate(Config config) {

        cronExpression = config.cronExpression();
        rootPagePath = config.rootPagePath();

        scheduler.unschedule(JOB_NAME);

        ScheduleOptions options = scheduler.EXPR(cronExpression);
        options.name(JOB_NAME);
        options.canRunConcurrently(false);

        boolean scheduled = scheduler.schedule(this, options);

        LOG.info("Page Publish Scheduler configured");
        LOG.info("Cron Expression: {}", cronExpression);
        LOG.info("Root Page Path: {}", rootPagePath);
        LOG.info("Scheduler registration successful: {}", scheduled);
    }

    @Override
    public void run() {

        LOG.info("========== Page Publish Scheduler Started ==========");

        int publishedCount = 0;
        int failedCount = 0;

        try (ResourceResolver resolver =
                resourceResolverFactory.getServiceResourceResolver(
                        Collections.singletonMap(
                                ResourceResolverFactory.SUBSERVICE,
                                SUBSERVICE))) {

            // Diagnostic logs
            LOG.info("Service user ID: {}", resolver.getUserID());

            LOG.info("Checking /content: {}",
                    resolver.getResource("/content") != null);

            LOG.info("Checking /content/demo: {}",
                    resolver.getResource("/content/demo") != null);

            LOG.info("Checking /content/demo/us: {}",
                    resolver.getResource("/content/demo/us") != null);

            LOG.info("Checking root page: {}",
                    resolver.getResource(rootPagePath) != null);

            LOG.info("Checking jcr:content: {}",
                    resolver.getResource(rootPagePath + "/jcr:content") != null);

            Resource rootResource = resolver.getResource(rootPagePath);

            if (rootResource == null) {
                LOG.error("Root page not found or not readable: {}", rootPagePath);
                return;
            }

            Resource rootContent = rootResource.getChild("jcr:content");

            if (rootContent == null) {
                LOG.error("Root page has no jcr:content node: {}", rootPagePath);
                return;
            }

            Session session = resolver.adaptTo(Session.class);

            if (session == null) {
                LOG.error("Unable to obtain JCR Session");
                return;
            }

            Deque<Resource> pages = new ArrayDeque<>();
            pages.push(rootResource);

            while (!pages.isEmpty()) {

                Resource currentPage = pages.pop();
                Resource contentResource = currentPage.getChild("jcr:content");

                if (contentResource != null) {

                    try {
                        replicator.replicate(
                                session,
                                ReplicationActionType.ACTIVATE,
                                currentPage.getPath());

                        publishedCount++;

                        LOG.info("Successfully published: {}",
                                currentPage.getPath());

                    } catch (ReplicationException e) {

                        failedCount++;

                        LOG.error("Failed to publish: {}",
                                currentPage.getPath(), e);
                    }
                }

                for (Resource child : currentPage.getChildren()) {

                    if (child.getChild("jcr:content") != null) {
                        pages.push(child);
                    }
                }
            }

            LOG.info("Total Successfully Published Pages: {}", publishedCount);
            LOG.info("Total Failed Pages: {}", failedCount);

        } catch (LoginException e) {

            LOG.error("Unable to obtain service resource resolver", e);

        } finally {

            LOG.info("========== Page Publish Scheduler Completed ==========");
        }
    }

    @Deactivate
    protected void deactivate() {

        scheduler.unschedule(JOB_NAME);

        LOG.info("Page Publish Scheduler stopped");
    }
}
