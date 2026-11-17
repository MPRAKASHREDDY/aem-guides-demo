package com.adobe.aem.guides.demo.core.jobs;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.demo.core.utills.RegionTreeUtil;

@Component(
    service = JobConsumer.class,
    immediate = true,
    property = {
        JobConsumer.PROPERTY_TOPICS + "=" + RegionTreeUtil.TOPIC_UPDATE
    }
)
public class RegionTreeUpdateJobConsumer implements JobConsumer {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionTreeUpdateJobConsumer.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public JobResult process(Job job) {

        LOG.info("UPDATE STEP 1: Region Tree Update Job Started");
        LOG.info("Update Job ID: {}", job.getId());

        Map<String, Object> info = new HashMap<>();
        info.put(ResourceResolverFactory.SUBSERVICE, RegionTreeUtil.SUBSERVICE);

        try (ResourceResolver rr =
                resolverFactory.getServiceResourceResolver(info)) {

            LOG.info("UPDATE STEP 2: Service resolver created");
            LOG.info("Resolver user: {}", rr.getUserID());

            Resource temp = rr.getResource(RegionTreeUtil.TEMP_PATH);

            LOG.info("UPDATE STEP 3: Temporary tree found: {}", temp != null);

            if (temp == null) {
                LOG.error("Temporary tree not found: {}", RegionTreeUtil.TEMP_PATH);
                return JobResult.FAILED;
            }

            Resource actual = rr.getResource(RegionTreeUtil.ACTUAL_PATH);

            if (actual == null) {

                LOG.info("Actual tree does not exist. Creating it.");

                Resource base = rr.getResource(RegionTreeUtil.BASE_PATH);

                if (base == null) {
                    LOG.error("Base path not accessible: {}", RegionTreeUtil.BASE_PATH);
                    return JobResult.FAILED;
                }

                Map<String, Object> rootProps = new HashMap<>();
                rootProps.put("jcr:primaryType", "nt:unstructured");

                actual = rr.create(base, "actual", rootProps);

                LOG.info("Actual tree created: {}", RegionTreeUtil.ACTUAL_PATH);
            }

            String[] createdNodes =
                    job.getProperty("createdNodes", String[].class);

            String[] updatedNodes =
                    job.getProperty("updatedNodes", String[].class);

            String[] movedNodes =
                    job.getProperty("movedNodes", String[].class);

            LOG.info("UPDATE STEP 4: Created nodes: {}",
                    createdNodes == null ? 0 : createdNodes.length);

            LOG.info("Updated nodes: {}",
                    updatedNodes == null ? 0 : updatedNodes.length);

            LOG.info("Moved nodes: {}",
                    movedNodes == null ? 0 : movedNodes.length);

            if (createdNodes != null) {
                for (String path : createdNodes) {
                    LOG.info("Creating node: {}", path);
                    copyNode(rr, path);
                }
            }

            if (updatedNodes != null) {
                for (String path : updatedNodes) {
                    LOG.info("Updating node: {}", path);
                    copyNode(rr, path);
                }
            }

            if (movedNodes != null) {

                for (String move : movedNodes) {

                    LOG.info("Processing moved node: {}", move);

                    String[] paths = move.split(">", 2);

                    if (paths.length != 2) {
                        throw new IllegalArgumentException(
                                "Invalid moved node path: " + move);
                    }

                    String oldPath = paths[0];
                    String newPath = paths[1];

                    copyNode(rr, newPath);

                    Resource oldResource = rr.getResource(oldPath);

                    if (oldResource != null) {
                        rr.delete(oldResource);
                        LOG.info("Old node deleted: {}", oldPath);
                    }
                }
            }

            rr.commit();

            LOG.info("UPDATE STEP 5: Actual tree changes committed");

            // Delete temporary tree after successful synchronization.
            temp = rr.getResource(RegionTreeUtil.TEMP_PATH);

            if (temp != null) {
                rr.delete(temp);
                rr.commit();

                LOG.info("UPDATE STEP 6: Temporary tree deleted");
            }

            LOG.info("UPDATE STEP 7: Region Tree Synchronization Completed");

            return JobResult.OK;

        } catch (Exception e) {
            LOG.error("Region Tree Update Job Failed", e);
            return JobResult.FAILED;
        }
    }

    private void copyNode(ResourceResolver rr, String actualPath)
            throws PersistenceException {

        LOG.info("Copying node to actual path: {}", actualPath);

        if (actualPath == null ||
                !actualPath.startsWith(RegionTreeUtil.ACTUAL_PATH + "/")) {

            throw new IllegalArgumentException(
                    "Invalid actual node path: " + actualPath);
        }

        String relativePath = actualPath.substring(
                RegionTreeUtil.ACTUAL_PATH.length());

        String tempPath = RegionTreeUtil.TEMP_PATH + relativePath;

        Resource source = rr.getResource(tempPath);

        if (source == null) {
            throw new IllegalStateException(
                    "Temporary source node not found: " + tempPath);
        }

        LOG.info("Temporary source found: {}", tempPath);

        String parentPath = actualPath.substring(
                0, actualPath.lastIndexOf('/'));

        String nodeName = actualPath.substring(
                actualPath.lastIndexOf('/') + 1);

        Resource parent = rr.getResource(parentPath);

        if (parent == null) {
            throw new IllegalStateException(
                    "Actual parent node not found: " + parentPath);
        }

        Resource target = rr.getResource(actualPath);

        Map<String, Object> properties =
                new HashMap<>(source.getValueMap());

        properties.put("jcr:primaryType", "nt:unstructured");

        if (target == null) {

            rr.create(parent, nodeName, properties);

            LOG.info("Node created successfully: {}", actualPath);

        } else {

            ModifiableValueMap targetProperties =
                    target.adaptTo(ModifiableValueMap.class);

            if (targetProperties == null) {
                throw new IllegalStateException(
                        "Cannot modify node: " + actualPath);
            }

            for (Map.Entry<String, Object> entry :
                    properties.entrySet()) {

                if (!"jcr:primaryType".equals(entry.getKey())) {
                    targetProperties.put(entry.getKey(), entry.getValue());
                }
            }

            LOG.info("Node updated successfully: {}", actualPath);
        }
    }
}