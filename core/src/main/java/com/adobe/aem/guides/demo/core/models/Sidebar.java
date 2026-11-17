package com.adobe.aem.guides.demo.core.models;


import java.util.Collections;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    adapters = Sidebar.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class Sidebar {

   @ValueMapValue
    private String logoPath;

    @ValueMapValue
    private String logoMobileImage;

    @ValueMapValue
    private String logoLink;

    @ValueMapValue
    private Boolean enableSwitch;

    @ValueMapValue
    private String country;

    @ChildResource(name = "headerLinks")
    private List<HeaderLink> headerLinks;

    @ChildResource(name = "navigationItems")
    private List<NavigationItem> navigationItems;

    public String getLogoPath() {
        return logoPath;
    }

    public String getLogoMobileImage() {
        return logoMobileImage;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public Boolean getEnableSwitch() {
        return enableSwitch;
    }

    public String getCountry() {
        return country;
    }

    public List<HeaderLink> getHeaderLinks() {
        return headerLinks != null
            ? Collections.unmodifiableList(headerLinks)
            : Collections.emptyList();
    }

    public List<NavigationItem> getNavigationItems() {
        return navigationItems != null
            ? Collections.unmodifiableList(navigationItems)
            : Collections.emptyList();
    }

}
