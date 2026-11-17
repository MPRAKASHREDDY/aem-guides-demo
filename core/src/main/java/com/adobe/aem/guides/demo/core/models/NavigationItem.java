package com.adobe.aem.guides.demo.core.models;
import java.util.Collections;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class NavigationItem {

@ValueMapValue
    private String desktopIcon;

    @ValueMapValue
    private String mobileIcon;

    @ChildResource(name = "navigationUrls")
    private List<NavigationUrl> navigationUrls;

    public String getDesktopIcon() {
        return desktopIcon;
    }

    public String getMobileIcon() {
        return mobileIcon;
    }

    public List<NavigationUrl> getNavigationUrls() {
        return navigationUrls != null
            ? Collections.unmodifiableList(navigationUrls)
            : Collections.emptyList();
    }

}
