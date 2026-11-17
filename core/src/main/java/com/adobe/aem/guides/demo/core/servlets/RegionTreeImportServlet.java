package com.adobe.aem.guides.demo.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.aem.guides.demo.core.utills.RegionTreeUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component(service = Servlet.class, immediate = true, property = {
        "sling.servlet.paths=/bin/regiontree/import",
        "sling.servlet.methods=POST"
})
public class RegionTreeImportServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final String OK_MSG =
            "The temp folder is created correctly!!";

    private static final String FAIL_MSG =
            "Error during the creation of the temp folder!!!";

    @Reference
    private transient JobManager jobManager;

    @Override
    protected void doPost(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws IOException {

        ResourceResolver rr = request.getResourceResolver();

        String user = rr.getUserID();

        // Authentication check
        if (user == null || "anonymous".equals(user)) {
            write(response, 401, "Authentication required");
            return;
        }

        Calendar start = Calendar.getInstance();

        try {

            // Step 1: Read JSON request
            String body = readBody(request);

            JsonObject root =
                    JsonParser.parseString(body).getAsJsonObject();

            JsonArray regions = root.getAsJsonArray("regions");

            if (regions == null) {
                throw new IllegalArgumentException(
                        "Missing 'regions' array");
            }

            // Step 2: Create region-tree if it does not exist
            Resource base = rr.getResource(RegionTreeUtil.BASE_PATH);

            if (base == null) {

                Resource conf = rr.getResource("/conf");

                if (conf == null) {
                    throw new IllegalStateException(
                            "Authenticated user cannot access /conf");
                }

                base = rr.create(conf, "region-tree", container());
            }

            // Step 3: Create actual folder if it does not exist
            Resource actual =
                    rr.getResource(RegionTreeUtil.ACTUAL_PATH);

            if (actual == null) {
                actual = rr.create(base, "actual", container());
            }

            // Step 4: Delete existing temp folder
            Resource oldTemp =
                    rr.getResource(RegionTreeUtil.TEMP_PATH);

            if (oldTemp != null) {
                rr.delete(oldTemp);
            }

            // Step 5: Create new temp folder
            Resource temp = rr.create(
                    base, "temp", container());

            // Step 6: Build Region -> Area -> Resort tree
            buildTree(rr, temp, regions);

            // Step 7: Create or update data node
            Resource data =
                    rr.getResource(RegionTreeUtil.DATA_PATH);

            if (data == null) {
                data = rr.create(base, "data", container());
            }

            ModifiableValueMap dataProps =
                    data.adaptTo(ModifiableValueMap.class);

            if (dataProps == null) {
                throw new IllegalStateException(
                        "Unable to adapt data resource to ModifiableValueMap");
            }

            dataProps.put("regionTreeJson", body);
            dataProps.put("startDateImport", start);
            dataProps.put("endDateImport", Calendar.getInstance());

            // Step 8: Commit repository changes
            rr.commit();

            // Step 9: Create background comparison job
            Map<String, Object> jobProps = new HashMap<>();

            jobProps.put("importedBy", user);

            if (jobManager.addJob(
                    RegionTreeUtil.TOPIC_TEMP, jobProps) == null) {
                throw new IllegalStateException(
                        "Job could not be created");
            }

            write(response, 200, OK_MSG);

        } catch (Exception e) {

            write(response, 500, FAIL_MSG);
        }

        // Do not close the request ResourceResolver.
        // Sling manages its lifecycle.
    }

    /**
     * Creates Region -> Area -> Resort hierarchy.
     */
    private void buildTree(ResourceResolver rr,
            Resource temp, JsonArray regions) throws Exception {

        for (JsonElement r : regions) {

            JsonObject region = r.getAsJsonObject();

            String rId = requireId(region);
            String rPath = rId;

            Resource rRes = rr.create(
                    temp,
                    rId,
                    RegionTreeUtil.buildProperties(
                            rId,
                            str(region, "name"),
                            str(region, "title"),
                            rId,
                            "",
                            rPath,
                            str(region, "damPath"),
                            str(region, "tagPath")));

            JsonArray areas = region.has("areas")
                    ? region.getAsJsonArray("areas")
                    : new JsonArray();

            for (JsonElement a : areas) {

                JsonObject area = a.getAsJsonObject();

                String aId = requireId(area);
                String aPath = rPath + "/" + aId;

                Resource aRes = rr.create(
                        rRes,
                        aId,
                        RegionTreeUtil.buildProperties(
                                aId,
                                str(area, "name"),
                                str(area, "title"),
                                rId,
                                aId,
                                aPath,
                                str(area, "damPath"),
                                str(area, "tagPath")));

                JsonArray resorts = area.has("resorts")
                        ? area.getAsJsonArray("resorts")
                        : new JsonArray();

                for (JsonElement s : resorts) {

                    JsonObject resort = s.getAsJsonObject();

                    String sId = requireId(resort);

                    rr.create(
                            aRes,
                            sId,
                            RegionTreeUtil.buildProperties(
                                    sId,
                                    str(resort, "name"),
                                    str(resort, "title"),
                                    rId,
                                    aId,
                                    aPath + "/" + sId,
                                    str(resort, "damPath"),
                                    str(resort, "tagPath")));
                }
            }
        }
    }

    /**
     * Validates the ID from JSON.
     */
    private static String requireId(JsonObject obj) {

        String id = str(obj, "id");

        if (!RegionTreeUtil.isValidId(id)) {
            throw new IllegalArgumentException(
                    "Invalid or missing id: " + id);
        }

        return id;
    }

    /**
     * Reads string values from JSON.
     */
    private static String str(JsonObject obj, String key) {

        JsonElement e = obj.get(key);

        return (e == null || e.isJsonNull())
                ? null
                : e.getAsString();
    }

    /**
     * Creates an unstructured JCR node.
     */
    private static Map<String, Object> container() {

        Map<String, Object> props = new HashMap<>();

        props.put("jcr:primaryType", "nt:unstructured");

        return props;
    }

    /**
     * Reads the incoming JSON body.
     */
    private static String readBody(
            SlingHttpServletRequest request) throws IOException {

        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {

            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }

        return sb.toString().trim();
    }

    /**
     * Writes JSON response.
     */
    private static void write(
            SlingHttpServletResponse response,
            int status,
            String message) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"status\":" + status
                        + ",\"message\":\"" + message + "\"}");
    }
}
