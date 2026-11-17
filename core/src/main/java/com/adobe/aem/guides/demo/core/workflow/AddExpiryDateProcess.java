package com.adobe.aem.guides.demo.core.workflow;


import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = WorkflowProcess.class,
    property = {
        "process.label=Add Expiry Date Process"
    }
)
public class AddExpiryDateProcess implements WorkflowProcess {

    private static final Logger LOG =
        LoggerFactory.getLogger(AddExpiryDateProcess.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public void execute(
            WorkItem workItem,
            WorkflowSession workflowSession,
            MetaDataMap args) throws WorkflowException {

        String pagePath = workItem.getWorkflowData()
                                  .getPayload()
                                  .toString();

        String contentPath = pagePath + "/jcr:content";

        LOG.info("Expiry date process started for: {}", pagePath);

        Map<String, Object> authInfo =
            Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,
                "data-writer");

        try (ResourceResolver resolver =
                resolverFactory.getServiceResourceResolver(authInfo)) {

            Resource contentResource =
                resolver.getResource(contentPath);

            if (contentResource == null) {
                LOG.error(
                    "Page jcr:content not found: {}",
                    contentPath);
                return;
            }

            ModifiableValueMap properties =
                contentResource.adaptTo(ModifiableValueMap.class);

            if (properties == null) {
                LOG.error(
                    "Unable to modify properties: {}",
                    contentPath);
                return;
            }

            Calendar currentDateTime = Calendar.getInstance();

            properties.put("expiryDate", currentDateTime);

            resolver.commit();

            LOG.info(
                "Expiry date successfully added to page: {}",
                contentPath);

        } catch (Exception e) {
            LOG.error(
                "Error while adding expiry date to: {}",
                contentPath, e);

            throw new WorkflowException(
                "Unable to add expiry date", e);
        }
    }
}