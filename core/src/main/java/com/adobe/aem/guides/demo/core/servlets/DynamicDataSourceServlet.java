package com.adobe.aem.guides.demo.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;

import org.apache.jackrabbit.JcrConstants;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import org.osgi.service.component.annotations.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;

@Component(
    service = Servlet.class,
    property = {
        SLING_SERVLET_RESOURCE_TYPES + "=demo/datasource/dropdowns",
        SLING_SERVLET_METHODS + "=GET"
    }
)

public class DynamicDataSourceServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG =
            LoggerFactory.getLogger(DynamicDataSourceServlet.class);
    private static final String DROPDOWN_SELECTOR = "dropdownSelector";

    private static final String COUNTRY_LIST = "countryList";
    private static final String COLOR_LIST = "colorList";
    private static final String FONT_LIST = "fontList";

   private static final String COUNTRY_PATH =
        "/content/dam/demo/demo-dropdowns/country.json";

private static final String COLOR_PATH =
        "/content/dam/demo/demo-dropdowns/color.json";

private static final String FONT_PATH =
        "/content/dam/demo/demo-dropdowns/font.json";

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

         LOG.error("========== DYNAMIC DATASOURCE SERVLET INVOKED ==========");
        String selector = null;

        ResourceResolver resourceResolver =
                request.getResourceResolver();

        try {
                LOG.error("######## DYNAMIC DATASOURCE VERSION 20261112_1600 ########");

            LOG.info("========== DYNAMIC DATASOURCE STARTED ==========");

            // STEP 1: Get current resource
            Resource currentResource = request.getResource();
            LOG.info("Current Resource:{}",currentResource);

            if (currentResource == null) {
                throw new IllegalStateException(
                        "Current resource is null");
            }

            LOG.info("Current Resource: {}",
                    currentResource.getPath());

            LOG.info("Current Resource Type: {}",
                    currentResource.getResourceType());

            // STEP 2: Resolve datasource resource
            Resource datasourceResource =
                    currentResource.getChild("datasource");

            if (datasourceResource == null) {
                datasourceResource = currentResource;
            }

            LOG.info("Datasource Resource: {}",
                    datasourceResource.getPath());

            // STEP 3: Read dropdown selector
            selector = datasourceResource
                    .getValueMap()
                    .get(DROPDOWN_SELECTOR, String.class);

            LOG.info("Dropdown Selector: {}", selector);

            if (selector == null || selector.trim().isEmpty()) {
                throw new IllegalStateException(
                        "dropdownSelector property is missing on: "
                                + datasourceResource.getPath());
            }

            // STEP 4: Resolve JSON path
            String jsonPath = getJsonPath(selector);

            LOG.info("Resolved JSON Path: {}", jsonPath);

            // STEP 5: Get JSON resource from DAM
            Resource jsonResource =
                    resourceResolver.getResource(jsonPath);

            LOG.info("JSON Resource Found: {}",
                    jsonResource != null);

            if (jsonResource == null) {
                throw new IllegalStateException(
                        "JSON resource not found: " + jsonPath);
            }

            // STEP 6: Resolve DAM asset
            Asset asset = DamUtil.resolveToAsset(jsonResource);

            LOG.info("DAM Asset Found: {}", asset != null);

            if (asset == null) {
                throw new IllegalStateException(
                        "DAM asset not found: " + jsonPath);
            }

            // STEP 7: Get original rendition
            Rendition original = asset.getOriginal();

            LOG.info("Original Rendition Found: {}",
                    original != null);

            if (original == null) {
                throw new IllegalStateException(
                        "Original rendition not found: " + jsonPath);
            }

            // STEP 8: Read JSON content
            StringBuilder jsonContent = new StringBuilder();

            try (InputStream inputStream =
                         original.adaptTo(InputStream.class)) {

                if (inputStream == null) {
                    throw new IllegalStateException(
                            "Unable to read JSON file: " + jsonPath);
                }

                try (BufferedReader reader =
                             new BufferedReader(
                                     new InputStreamReader(
                                             inputStream,
                                             StandardCharsets.UTF_8))) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                }
            }

            LOG.info("JSON Content Length: {}",
                    jsonContent.length());

            // STEP 9: Parse JSON array
            JSONArray jsonArray =
                    new JSONArray(jsonContent.toString());

            LOG.info("JSON Array Size: {}", jsonArray.length());

            // STEP 10: Create dropdown option resources
            List<Resource> options = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject =
                        jsonArray.getJSONObject(i);

                String text = jsonObject.getString("text");
                String value = jsonObject.getString("value");

                Map<String, Object> properties = new HashMap<>();

                properties.put("text", text);
                properties.put("value", value);

                ValueMap valueMap =
                        new ValueMapDecorator(properties);

                Resource optionResource =
                        new ValueMapResource(
                                resourceResolver,
                                new ResourceMetadata(),
                                JcrConstants.NT_UNSTRUCTURED,
                                valueMap);

                options.add(optionResource);

                LOG.debug("Added option: {} = {}", text, value);
            }

            // STEP 11: Create Granite DataSource
            Iterator<Resource> iterator = options.iterator();

            DataSource dataSource =
                    new SimpleDataSource(iterator);

            // STEP 12: Attach DataSource to request
            request.setAttribute(
                    DataSource.class.getName(),
                    dataSource);

            LOG.info(
                    "SUCCESS: Created datasource for selector: {} with {} options",
                    selector,
                    options.size());

            LOG.info("========== DYNAMIC DATASOURCE COMPLETED ==========");

        } catch (Exception e) {

            LOG.error(
                    "FAILED: Error creating datasource for selector: {}",
                    selector,
                    e);

            // Return an empty datasource instead of calling response.sendError().
            // This avoids Response already committed errors during dialog rendering.

            request.setAttribute(
                    DataSource.class.getName(),
                    new SimpleDataSource(
                            Collections.<Resource>emptyList().iterator()));

            LOG.warn(
                    "Returning empty datasource for selector: {}",
                    selector);
        }
    }

    // STEP 13: Identify JSON file using dropdown selector
    private String getJsonPath(String selector) {

        switch (selector) {

            case COUNTRY_LIST:
                return COUNTRY_PATH;

            case COLOR_LIST:
                return COLOR_PATH;

            case FONT_LIST:
                return FONT_PATH;

            default:
                throw new IllegalArgumentException(
                        "Invalid dropdown selector: " + selector);
        }
    }
}
