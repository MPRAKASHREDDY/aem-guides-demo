package com.adobe.aem.guides.demo.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(
    adaptables = {Resource.class, SlingHttpServletRequest.class},
    adapters = {PagePaginationModel.class, ComponentExporter.class},
    resourceType = "demo/components/page-pagination",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class PagePaginationModel implements ComponentExporter {

    @ValueMapValue
    private String parentPagePath;

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource resource;

    private List<String> pages = new ArrayList<>();

    @PostConstruct
    protected void init() {

        if (parentPagePath == null || parentPagePath.isEmpty()) {
            return;
        }

        PageManager pageManager =
                resourceResolver.adaptTo(PageManager.class);

        if (pageManager == null) {
            return;
        }

        Page parentPage = pageManager.getPage(parentPagePath);

        if (parentPage == null) {
            return;
        }

        // Add parent page first
        List<String> allPages = new ArrayList<>();
        allPages.add(parentPage.getPath());

        // Get direct child pages
        Iterator<Page> children = parentPage.listChildren();

        while (children.hasNext()) {
            Page child = children.next();
            allPages.add(child.getPath());
        }

        // Read URL parameters
        int pageSize = getParameter("pageSize", 1);
        int pageLimit = getParameter("pageLimit", 3);

        // Validate parameters
        if (pageSize < 1 || pageLimit < 1) {
            return;
        }

        // Calculate pagination indexes
        long start = (long) (pageSize - 1) * pageLimit;

        if (start >= allPages.size()) {
            return;
        }

        int startIndex = (int) start;
        int endIndex = Math.min(
                startIndex + pageLimit,
                allPages.size()
        );

        // Extract requested pages
        pages = new ArrayList<>(
                allPages.subList(startIndex, endIndex)
        );
    }

    private int getParameter(String name, int defaultValue) {

        if (request == null) {
            return defaultValue;
        }

        String value = request.getParameter(name);

        try {
            return value == null ? defaultValue :
                    Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @JsonProperty("pages")
    public List<String> getPages() {
        return Collections.unmodifiableList(pages);
    }

    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }
}
