package com.adobe.aem.guides.demo.core.listeners;

import org.osgi.service.component.annotations.Component;
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
        EventConstants.EVENT_TOPIC + "=" +
        ReplicationAction.EVENT_TOPIC
    }
)
public class Task11ReplicationEventListener
        implements EventHandler {

    private static final Logger LOG =
        LoggerFactory.getLogger(
            Task11ReplicationEventListener.class
        );

    @Override
    public void handleEvent(Event event) {

        try {

            ReplicationAction action =
                ReplicationAction.fromEvent(event);

            if (action == null) {

                LOG.warn(
                    "Task-11: Unable to convert replication event."
                );

                return;
            }

            ReplicationActionType actionType =
                action.getType();

            String pagePath = action.getPath();

            if (actionType == ReplicationActionType.ACTIVATE) {

                LOG.info(
                    "Task-11: PAGE PUBLISHED: {}",
                    pagePath
                );

            } else if (
                actionType == ReplicationActionType.DEACTIVATE
            ) {

                LOG.info(
                    "Task-11: PAGE UNPUBLISHED: {}",
                    pagePath
                );

            } else {

                LOG.debug(
                    "Task-11: Other replication event: {} for {}",
                    actionType,
                    pagePath
                );
            }

        } catch (Exception e) {

            LOG.error(
                "Task-11: Error processing replication event.",
                e
            );
        }
    }
}