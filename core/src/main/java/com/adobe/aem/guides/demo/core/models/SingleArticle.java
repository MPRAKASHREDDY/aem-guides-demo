package com.adobe.aem.guides.demo.core.models;

import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;


@Model(
    adaptables = Resource.class,
    resourceType = "demo/components/single-article",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SingleArticle {

     @ValueMapValue
    private boolean enablePII;

    @ValueMapValue
    private String cssClass;

    public boolean isEnablePII() {
        return enablePII;
    }

    public String getCssClass() {
        return cssClass;
    }

}
