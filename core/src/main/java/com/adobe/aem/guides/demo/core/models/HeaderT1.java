
package com.adobe.aem.guides.demo.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    adapters = HeaderT1.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class HeaderT1 {

    @ValueMapValue
    private String headerTitle;

    @ValueMapValue
    private String headerLink;

    @ValueMapValue
    private Boolean enableHeader;

    @ValueMapValue
    private String buttonText;

    @ValueMapValue
    private String buttonLink;

    @ChildResource(name = "menuItems")
    private Resource menuItems;

    private List<NavigationItem> buttons;

    @PostConstruct
    protected void init() {
        buttons = new ArrayList<>();

        if (menuItems == null) {
            return;
        }

        for (Resource resource : menuItems.getChildren()) {
            ValueMap properties = resource.getValueMap();

            String text = properties.get("buttonText", String.class);
            String link = properties.get("buttonLink", String.class);
            String icon = properties.get("buttonIcon", String.class);

            buttons.add(new NavigationItem(text, link, icon));
        }
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public String getHeaderLink() {
        return headerLink;
    }

    public Boolean getEnableHeader() {
        return enableHeader != null ? enableHeader : Boolean.TRUE;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getButtonLink() {
        return buttonLink;
    }

    public List<NavigationItem> getButtons() {
        return buttons != null
            ? Collections.unmodifiableList(buttons)
            : Collections.emptyList();
    }

    public static class NavigationItem {
        private final String buttonText;
        private final String buttonLink;
        private final String buttonIcon;

        public NavigationItem(
                String buttonText,
                String buttonLink,
                String buttonIcon) {
            this.buttonText = buttonText;
            this.buttonLink = buttonLink;
            this.buttonIcon = buttonIcon;
        }

        public String getButtonText() {
            return buttonText;
        }

        public String getButtonLink() {
            return buttonLink;
        }

        public String getButtonIcon() {
            return buttonIcon;
        }
    }
}
