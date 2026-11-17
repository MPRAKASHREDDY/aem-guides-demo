package com.adobe.aem.guides.demo.core.models;

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
public class ProjectModel {
     @ValueMapValue
    private String projectName;

    @ChildResource
    private List<TechnologyModel> technologies;

    public String getProjectName() {
        return projectName;
    }

    public List<TechnologyModel> getTechnologies() {
        return technologies;
    }
}
