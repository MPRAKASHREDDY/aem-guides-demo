package com.adobe.aem.guides.demo.core.listeners;


import java.util.Iterator;
import java.util.Collections;

import javax.jcr.Session;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.adobe.granite.workflow.exec.WorkflowData;

import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import com.day.cq.workflow.WorkflowService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = EventHandler.class,
    immediate = true,
    property = {
        EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC
    }
)
public class PageCreationEventHandler implements EventHandler {

    private static final Logger LOG =
        LoggerFactory.getLogger(PageCreationEventHandler.class);

    private static final String WORKFLOW_MODEL =
        "/var/workflow/models/demo-expiry-workflow";
    private static final String SUBSERVICE = "data-writer";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private WorkflowService workflowService;

    @Override
    public void handleEvent(Event event) {

        PageEvent pageEvent = PageEvent.fromEvent(event);

        if (pageEvent == null) {
            return;
        }

        Iterator<PageModification> modifications =
            pageEvent.getModifications();

        if (modifications == null) {
            return;
        }

        while (modifications.hasNext()) {

            PageModification modification = modifications.next();

            if (modification == null ||
                    !PageModification.ModificationType.CREATED
                        .equals(modification.getType())) {
                continue;
            }

            String pagePath = modification.getPath();

            if (pagePath == null ||
                    !pagePath.startsWith("/content/demo/")) {
                continue;
            }

            LOG.info("New page created: {}", pagePath);

            try (ResourceResolver resolver =
                    resolverFactory.getServiceResourceResolver(
                        Collections.singletonMap(
                            ResourceResolverFactory.SUBSERVICE,
                            SUBSERVICE))) {

                Session session = resolver.adaptTo(Session.class);

                if (session == null) {
                    LOG.error(
                        "Unable to adapt session for page: {}",
                        pagePath);
                    continue;
                }

                WorkflowSession workflowSession =
                    (WorkflowSession) workflowService.getWorkflowSession(session);

                if (workflowSession == null) {
                    LOG.error(
                        "Workflow session is unavailable for page: {}",
                        pagePath);
                    continue;
                }

                WorkflowModel model =
                    workflowSession.getModel(WORKFLOW_MODEL);

                if (model == null) {
                    LOG.error(
                        "Workflow model '{}' not found for page: {}",
                        WORKFLOW_MODEL,
                        pagePath);
                    continue;
                }

                WorkflowData workflowData =
                    workflowSession.newWorkflowData(
                        "JCR_PATH", pagePath);

                workflowSession.startWorkflow(
                    model, workflowData);

                LOG.info(
                    "Expiry workflow started for page: {}",
                    pagePath);

            } catch (Exception e) {
                LOG.error(
                    "Unable to start expiry workflow for page: {}",
                    pagePath, e);
            }
        }
    }
}