package com.adobe.aem.guides.demo.core.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = ResourceChangeListener.class,
    property = {
        ResourceChangeListener.PATHS + "=/content/dam/images",
        ResourceChangeListener.CHANGES + "=CHANGED"
    }
)
public class DamAssetChangeListener implements ResourceChangeListener {
   
    private static final Logger LOG =
            LoggerFactory.getLogger(DamAssetChangeListener.class);
   

    public static final String JOB_TOPIC =
            "com/adobe/aem/guides/demo/publish-dam-asset";

    private static final String DAM_PATH = "/content/dam/images";

    @Reference
    private JobManager jobManager;

    @Override
    public void onChange(List<ResourceChange> changes) {

        for (ResourceChange change : changes) {

            if (change.getType() != ResourceChange.ChangeType.CHANGED) {
                continue;
            }

            String changedPath = change.getPath();

            // Find the actual asset path
            int index = changedPath.indexOf("/jcr:content");

            String assetPath = index != -1
                    ? changedPath.substring(0, index)
                    : changedPath;

            // Ensure the asset belongs to the configured folder
            if (!assetPath.startsWith(DAM_PATH + "/")) {
                continue;
            }

            // Create Sling Job
            Map<String, Object> properties = new HashMap<>();
            properties.put("assetPath", assetPath);

            jobManager.addJob(JOB_TOPIC, properties);

            LOG.info("Sling Job created for modified asset: {}", assetPath);
        }
    }
}
