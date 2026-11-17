package com.adobe.aem.guides.demo.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

@Component(
    service = WorkflowProcess.class,
    property = {
        "process.label=First Workflow Java"
    }
)
public class FirstWork implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(FirstWork.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        LOG.info("Inside First Workflow Java");

        String payloadPath = workItem.getWorkflowData().getPayload().toString();
        LOG.info("Payload Path: {}", payloadPath);
        
    }
    
}
