package com.adobe.aem.guides.demo.core.models;

import java.util.Collections;
import java.util.List;

import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SlideItem {

     @ValueMapValue
        private String title;

        @ValueMapValue
        private String linkPath;

        @ChildResource(name = "tags")
        private List<TagItem> tags;

        public String getTitle() {
            return title;
        }

        public String getLinkUrl() {
            if (linkPath == null || linkPath.trim().isEmpty()) {
                return null;
            }
            return linkPath.startsWith("/content") && !linkPath.contains(".")
                    ? linkPath + ".html"
                    : linkPath;
        }

        public List<TagItem> getTags() {
            return tags != null ? tags : Collections.emptyList();
        }

}
