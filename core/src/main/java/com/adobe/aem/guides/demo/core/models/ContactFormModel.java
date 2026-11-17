package com.adobe.aem.guides.demo.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import com.adobe.aem.guides.demo.core.utills.ContactConstants;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContactFormModel {

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    private String id = "";
    private String name = "";
    private String email = "";
    private String mobile = "";
    private boolean editing;

    @PostConstruct
    protected void init() {
        String param = request.getParameter("id");
        if (param == null || !ContactConstants.ID_PATTERN.matcher(param).matches()) {
            return;
        }
        Resource contact = resourceResolver.getResource(ContactConstants.BASE_PATH + "/" + param);
        if (contact == null) {
            return;
        }
        ValueMap vm = contact.getValueMap();
        id = param;
        name = vm.get("name", "");
        email = vm.get("email", "");
        mobile = vm.get("mobile", "");
        editing = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public boolean isEditing() {
        return editing;
    }
}