package com.adobe.aem.guides.demo.core.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobe.aem.guides.demo.core.utills.ContactConstants;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FormDataTableModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    private final List<ContactItem> contacts = new ArrayList<>();

    @PostConstruct
    protected void init() {
        String query = "SELECT * FROM [nt:unstructured] AS s WHERE ISCHILDNODE(s, '"
                + ContactConstants.BASE_PATH + "')";

        Iterator<Resource> results = resourceResolver.findResources(query, "JCR-SQL2");
        while (results.hasNext()) {
            Resource r = results.next();
            ValueMap vm = r.getValueMap();
            contacts.add(new ContactItem(
                    r.getName(),
                    vm.get("name", ""),
                    vm.get("email", ""),
                    vm.get("mobile", ""),
                    vm.get("createdAt", 0L)));
        }
        contacts.sort(Comparator.comparingLong(ContactItem::getCreatedAt));
    }

    public List<ContactItem> getContacts() {
        return contacts;
    }
}
