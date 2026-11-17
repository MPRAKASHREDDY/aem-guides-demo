package com.adobe.aem.guides.demo.core.servlets;


import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.osgi.service.component.annotations.Component;

import com.day.cq.dam.api.Asset;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/demo/images",
        "sling.servlet.methods=GET"
    }
)
public class ImagesServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG =
            LoggerFactory.getLogger(ImagesServlet.class);

    private static final String DAM_FOLDER =
            "/content/dam/images";

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray images = new JSONArray();

        Resource folder = request.getResourceResolver()
                .getResource(DAM_FOLDER);

        if (folder == null) {

            LOG.error("DAM folder not found: {}", DAM_FOLDER);

            response.setStatus(
                    SlingHttpServletResponse.SC_NOT_FOUND);

            response.getWriter().write(
                    "{\"error\":\"DAM folder not found\"}");

            return;
        }

        Iterator<Resource> children = folder.listChildren();

        while (children.hasNext()) {

            Resource resource = children.next();

            Asset asset = resource.adaptTo(Asset.class);

            if (asset != null &&
                    asset.getMimeType() != null &&
                    asset.getMimeType().startsWith("image/")) {

                try {

                    JSONObject image = new JSONObject();

                    image.put("name", asset.getName());
                    image.put("path", asset.getPath());

                    images.put(image);

                    LOG.info("Image found: {}", asset.getPath());

                } catch (Exception e) {

                    LOG.error(
                            "Error processing asset: {}",
                            asset.getPath(), e);
                }
            }
        }

        response.getWriter().write(images.toString());

        LOG.info("Total images fetched: {}", images.length());
    }
}
