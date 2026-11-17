package com.adobe.aem.guides.demo.core.models;


import java.util.Collections;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

@Model(
        adaptables = SlingHttpServletRequest.class,
        resourceType = "demo/components/customcarousel",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class CarouselT2 {

    @ChildResource(name = "slides")
    private List<SlideItem> slides;

    public List<SlideItem> getSlides() {
        return slides != null ? slides : Collections.emptyList();
    }

    public boolean isEmpty() {
        return getSlides().isEmpty();
    }

}
