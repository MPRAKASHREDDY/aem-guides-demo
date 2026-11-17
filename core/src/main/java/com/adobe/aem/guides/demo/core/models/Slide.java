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
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class Slide {


    // Header tab fields
    @ValueMapValue
    private String logoPath;

    @ValueMapValue
    private String logoMobileImage;

    @ValueMapValue
    private String logoLink;

    @ValueMapValue
    private Boolean enableSwitch;

    // Region tab field
    @ValueMapValue
    private String country;

    // Header Links multifield
    @ChildResource(name = "headerLinks")
    private List<HeaderLink> headerLinks;

    // Sidebar Navigation multifield
    @ChildResource(name = "navigationItems")
    private List<NavigationItem> navigationItems;

    public Slide(String title, String path, List<String> subItems) {
        //TODO Auto-generated constructor stub
    }

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
