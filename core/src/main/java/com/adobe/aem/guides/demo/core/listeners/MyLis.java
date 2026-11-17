package com.adobe.aem.guides.demo.core.listeners;
import java.util.List;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = ResourceChangeListener.class,
    property = {
        ResourceChangeListener.PATHS + "=/content/demo/us/en",
        ResourceChangeListener.CHANGES + "=ADDED",
        ResourceChangeListener.CHANGES + "=CHANGED",
        ResourceChangeListener.CHANGES + "=REMOVED"
    }
)
public class MyLis implements ResourceChangeListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(MyLis.class);

    @Override
    public void onChange(List<ResourceChange> changes) {
        LOG.info("MyLis onChange method called");

        for (ResourceChange change : changes) {
            LOG.info(
                "Resource event: {} at: {} isExternal: {}",
                change.getType(),
                change.getPath(),
                change.isExternal()
            );
        }
    }

}
