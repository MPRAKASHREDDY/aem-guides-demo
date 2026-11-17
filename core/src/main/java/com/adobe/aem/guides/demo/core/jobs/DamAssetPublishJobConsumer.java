package com.adobe.aem.guides.demo.core.jobs;

import java.util.Collections;
import java.util.Map;

import javax.jcr.Session;


import com.adobe.aem.guides.demo.core.listeners.DamAssetChangeListener;
import com.day.cq.dam.api.Asset;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = JobConsumer.class,
    property = {
        JobConsumer.PROPERTY_TOPICS + "=" +
        DamAssetChangeListener.JOB_TOPIC
    }
)
public class DamAssetPublishJobConsumer implements JobConsumer {

    private static final Logger LOG =
            LoggerFactory.getLogger(DamAssetPublishJobConsumer.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private Replicator replicator;

    @Override
    public JobResult process(Job job) {

        String assetPath = job.getProperty("assetPath", String.class);

        if (assetPath == null || assetPath.trim().isEmpty()) {
            LOG.error("Asset path is missing");
            return JobResult.CANCEL;
        }

        if (!assetPath.startsWith("/content/dam/images/")) {
            LOG.warn("Asset outside configured folder: {}", assetPath);
            return JobResult.CANCEL;
        }

        ResourceResolver resolver = getServiceResolver();

        if (resolver == null) {
            return JobResult.FAILED;
        }

        try {

            Resource resource = resolver.getResource(assetPath);

            if (resource == null) {
                LOG.warn("Asset not found: {}", assetPath);
                return JobResult.CANCEL;
            }

            Asset asset = resource.adaptTo(Asset.class);

            if (asset == null) {
                LOG.warn("Not a DAM asset: {}", assetPath);
                return JobResult.CANCEL;
            }

            String mimeType = asset.getMimeType();

            if (mimeType == null || !mimeType.startsWith("image/")) {
                LOG.warn("Not an image: {}", assetPath);
                return JobResult.CANCEL;
            }

            Session session = resolver.adaptTo(Session.class);

            if (session == null) {
                LOG.error("JCR session unavailable");
                return JobResult.FAILED;
            }

            replicator.replicate(
                    session,
                    ReplicationActionType.ACTIVATE,
                    assetPath
            );

            LOG.info("Successfully published image: {}", assetPath);

            return JobResult.OK;

        } catch (ReplicationException e) {

            LOG.error("Publishing failed: {}", assetPath, e);
            return JobResult.FAILED;

        } finally {
            resolver.close();
        }
    }

    private ResourceResolver getServiceResolver() {

        Map<String, Object> params = Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,
                "asset-publisher"
        );

        try {
    return resolverFactory.getServiceResourceResolver(params);

} catch (Exception e) {
    LOG.error("Unable to obtain service ResourceResolver", e);
    return null;
}
    }
}
