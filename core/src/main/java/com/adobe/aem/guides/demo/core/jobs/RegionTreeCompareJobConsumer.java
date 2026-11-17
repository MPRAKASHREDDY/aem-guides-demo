package com.adobe.aem.guides.demo.core.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.Session;

 
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import com.adobe.aem.guides.demo.core.utills.RegionTreeUtil;
import com.adobe.aem.guides.demo.core.utills.RegionTreeUtil.NodeInfo;
 
@Component(
    service = JobConsumer.class,
    immediate = true,
    property = {
        JobConsumer.PROPERTY_TOPICS + "=" + RegionTreeUtil.TOPIC_TEMP
    }
)
public class RegionTreeCompareJobConsumer implements JobConsumer {
 
    private static final Logger LOG =
            LoggerFactory.getLogger(RegionTreeCompareJobConsumer.class);
 
    @Reference
    private ResourceResolverFactory resolverFactory;
 
    @Reference
    private JobManager jobManager;
 
    @Override
    public JobResult process(Job job) {
 
        LOG.info("========== REGION TREE COMPARE JOB STARTED ==========");
        LOG.info("Job ID: {}", job.getId());
        LOG.info("Job Topic: {}", job.getTopic());
 
        Map<String, Object> info = new HashMap<>();
        info.put(
                ResourceResolverFactory.SUBSERVICE,
                RegionTreeUtil.SUBSERVICE
        );
 
        try (ResourceResolver rr =
                resolverFactory.getServiceResourceResolver(info)) {
 
            LOG.info("Service resolver successfully created");
            LOG.info("Service resolver user: {}", rr.getUserID());
 
        //     ---- TEMP DIAGNOSTIC (remove after fixing) ----
            Session session = rr.adaptTo(Session.class);
            LOG.info("DIAG session userId: {}", session.getUserID());
            LOG.info("DIAG hasPermission read on {}: {}",
                    RegionTreeUtil.TEMP_PATH,
                    session.hasPermission(
                            RegionTreeUtil.TEMP_PATH, Session.ACTION_READ));
            LOG.info("DIAG nodeExists {}: {}",
                    RegionTreeUtil.TEMP_PATH,
                    session.nodeExists(RegionTreeUtil.TEMP_PATH));
            // ---- END DIAGNOSTIC ----
 
            Resource base = rr.getResource(RegionTreeUtil.BASE_PATH);
            Resource temp = rr.getResource(RegionTreeUtil.TEMP_PATH);
            Resource actual = rr.getResource(RegionTreeUtil.ACTUAL_PATH);
 
            LOG.info("Base path accessible: {}", base != null);
            LOG.info("Temp path accessible: {}", temp != null);
            LOG.info("Actual path accessible: {}", actual != null);
 
            if (temp == null) {
                LOG.error(
                    "Temporary tree not found at {}",
                    RegionTreeUtil.TEMP_PATH
                );
                // Permanent problem: retrying will not help
                return JobResult.CANCEL;
            }
 
            Map<String, NodeInfo> tempMap =
                    RegionTreeUtil.flatten(temp);
 
            // On the first run /conf/region-tree/actual may not exist yet
            Map<String, NodeInfo> actualMap;
            if (actual == null) {
                LOG.warn(
                    "Actual tree not found at {}, treating all temp nodes as created",
                    RegionTreeUtil.ACTUAL_PATH
                );
                actualMap = new HashMap<>();
            } else {
                actualMap = RegionTreeUtil.flatten(actual);
            }
 
            LOG.info(
                "Loaded temp nodes: {}, actual nodes: {}",
                tempMap.size(),
                actualMap.size()
            );
 
            List<String> listCreatedNode = new ArrayList<>();
            List<String> listUpdatedNode = new ArrayList<>();
            List<String> listMovedNode = new ArrayList<>();
 
            RegionTreeUtil.compare(
                    tempMap,
                    actualMap,
                    listCreatedNode,
                    listUpdatedNode,
                    listMovedNode
            );
 
            LOG.info("Comparison completed");
            LOG.info("Created nodes: {}", listCreatedNode);
            LOG.info("Updated nodes: {}", listUpdatedNode);
            LOG.info("Moved nodes: {}", listMovedNode);
 
            Map<String, Object> properties = new HashMap<>();
 
            properties.put(
                    "createdNodes",
                    listCreatedNode.toArray(new String[0])
            );
 
            properties.put(
                    "updatedNodes",
                    listUpdatedNode.toArray(new String[0])
            );
 
            properties.put(
                    "movedNodes",
                    listMovedNode.toArray(new String[0])
            );
 
            LOG.info(
                "Submitting update job with topic: {}",
                RegionTreeUtil.TOPIC_UPDATE
            );
 
            Job updateJob = jobManager.addJob(
                    RegionTreeUtil.TOPIC_UPDATE,
                    properties
            );
 
            if (updateJob == null) {
                LOG.error("Failed to submit region tree update job");
                return JobResult.FAILED;
            }
 
            LOG.info(
                "Update job submitted successfully. Job ID: {}",
                updateJob.getId()
            );
 
            // // Delete temp only after comparison and update-job submission.
            rr.delete(temp);
            rr.commit();
 
            LOG.info(
                "Temporary tree deleted successfully: {}",
                RegionTreeUtil.TEMP_PATH
            );
 
            LOG.info("========== REGION TREE COMPARE JOB COMPLETED ==========");
 
            return JobResult.OK;
 
        } catch (LoginException e) {
            // Missing or wrong service user mapping: retrying will not help
            LOG.error(
                "Could not get service resolver for subservice {}",
                RegionTreeUtil.SUBSERVICE,
                e
            );
            return JobResult.CANCEL;
 
        } catch (Exception e) {
            LOG.error(
                "Exception occurred during region tree comparison",
                e
            );
            return JobResult.FAILED;
        }
    }
}