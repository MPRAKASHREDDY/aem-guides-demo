package com.adobe.aem.guides.demo.core.workflow;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
    service = ParticipantStepChooser.class,
    property = {
        ParticipantStepChooser.SERVICE_PROPERTY_LABEL
            + "=Demo Dynamic Participant Chooser"
    }
)
public class CustomDynamicParticipant implements ParticipantStepChooser {

    private static final Logger LOG =
            LoggerFactory.getLogger(CustomDynamicParticipant.class);

  @Override
public String getParticipant(
        WorkItem workItem,
        WorkflowSession workflowSession,
        MetaDataMap args) throws WorkflowException {

    LOG.info("========== Dynamic Participant Started ==========");

    String pagePath = workItem.getWorkflowData()
                              .getPayload()
                              .toString();

    LOG.info("Received Page Path: {}", pagePath);

    String participant;

    if (pagePath.equals("/content/demo/us/en/hr")) {

        participant = "hrmanager";

    } else if (pagePath.equals("/content/demo/us/en/it")) {

        participant = "itmanager";

    } else if (pagePath.equals("/content/demo/us/en/finance")) {

        participant = "financemanager";

    } else {

        participant = "admin";
    }

    LOG.info("Selected Participant: {}", participant);

    if (participant == null || participant.trim().isEmpty()) {
        LOG.error("Participant is null or empty. Assigning admin.");
        participant = "admin";
    }

    LOG.info("Returning Participant: {}", participant);

    LOG.info("========== Dynamic Participant Completed ==========");

    return participant;
}
}
