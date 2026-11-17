 package com.adobe.aem.guides.demo.core.listeners;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = EventHandler.class,
    immediate = true,
    property = {
        EventConstants.EVENT_TOPIC + "=" + ReplicationAction.EVENT_TOPIC
    }
)
public class PagePublishEventHandler implements EventHandler {

    private static final Logger LOG =
            LoggerFactory.getLogger(PagePublishEventHandler.class);

    private static final String SUBSERVICE = "page-writer";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void handleEvent(Event event) {

        // Get replication action from event
        ReplicationAction action = ReplicationAction.fromEvent(event);

        if (action == null) {
            return;
        }

        // Check whether the page is published (ACTIVATE)
        if (action.getType() != ReplicationActionType.ACTIVATE) {
            return;
        }

        // Get published page path
        String pagePath = action.getPath();

        LOG.info("Page published successfully: {}", pagePath);

        // Update changed property under jcr:content
        updatePageProperty(pagePath);
    }

    private void updatePageProperty(String pagePath) {

        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(
            ResourceResolverFactory.SUBSERVICE,
            SUBSERVICE
        );

        try (ResourceResolver resolver =
                resourceResolverFactory.getServiceResourceResolver(authInfo)) {

            // Get jcr:content resource
            Resource contentResource =
                    resolver.getResource(pagePath + "/jcr:content");

            if (contentResource == null) {
                LOG.error("jcr:content not found for page: {}", pagePath);
                return;
            }

            // Get modifiable properties
            ModifiableValueMap properties =
                    contentResource.adaptTo(ModifiableValueMap.class);

            if (properties == null) {
                LOG.error("Unable to modify properties: {}", pagePath);
                return;
            }

            // Add Boolean property
            properties.put("changed", true);

            // Save changes
            resolver.commit();

            LOG.info(
                "Successfully added changed=true to: {}",
                pagePath + "/jcr:content"
            );

        } catch (Exception e) {
            LOG.error(
                "Error updating page property for: {}",
                pagePath,
                e
            );
        }
    }
}