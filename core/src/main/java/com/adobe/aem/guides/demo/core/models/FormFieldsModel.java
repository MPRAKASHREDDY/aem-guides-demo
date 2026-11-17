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
public class FormFieldsModel {
     @ValueMapValue
    private String name;

    @ValueMapValue
    private Integer age;

    @ValueMapValue
    private Boolean active;

    @ValueMapValue
    private String gender;

    @ValueMapValue
    private String pagePath;

    @ValueMapValue
    private String backgroundColor;

    @ValueMapValue
    private String joiningDate;

    @ValueMapValue
    private String[] skills;

    @ChildResource
    private List<ProjectModel> projects;

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Boolean getActive() {
        return active;
    }

    public String getGender() {
        return gender;
    }

    public String getPagePath() {
        return pagePath;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public String[] getSkills() {
        return skills;
    }

    public List<ProjectModel> getProjects() {
        return projects;
    }
}
