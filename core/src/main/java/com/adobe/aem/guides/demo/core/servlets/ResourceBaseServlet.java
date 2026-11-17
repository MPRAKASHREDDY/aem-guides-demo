package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
@Component(
    service=Servlet.class,
    property={
        "sling.servlet.resourceTypes=demo/components/formfields",
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=json"
    }
)

public class ResourceBaseServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

     @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Resource resource = request.getResource();
        
        response.setHeader("X-Debug-Servlet", "ResourceBaseServlet");

        ValueMap properties = resource.getValueMap();

        JSONObject json = new JSONObject();

        try {
            json.put("name",
                properties.get("name", ""));

            json.put("age",
                properties.get("age", ""));
            response.getWriter().write(json.toString());

        } catch (JSONException e) {
            response.setStatus(
                SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.getWriter().write(
                "{\"error\":\"Unable to generate JSON response\"}"
            );
        }
    }
}