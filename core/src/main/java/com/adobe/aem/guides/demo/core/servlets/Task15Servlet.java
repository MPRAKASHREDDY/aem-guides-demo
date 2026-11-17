package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.aem.guides.demo.core.services.Task15UpdateService;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/task15/update",
        "sling.servlet.methods=POST"
    }
)
public class Task15Servlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private Task15UpdateService updateService;

    @Override
    protected void doPost(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read JSON body from Postman
            String requestBody = request.getReader()
                    .lines()
                    .collect(Collectors.joining());

            JSONObject json = new JSONObject(requestBody);

            // Extract values sent by Postman
            String clientId = json.getString("clientId");
            String apiToken = json.getString("apiToken");

            // Pass Postman values to the service
            updateService.updatePageProperties(clientId, apiToken);

            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.getWriter().write(
                "Successfully updated clientId and apiToken from Postman"
            );

        } catch (IllegalArgumentException e) {

            response.sendError(
                SlingHttpServletResponse.SC_BAD_REQUEST,
                e.getMessage()
            );

        } catch (Exception e) {

            response.sendError(
                SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error updating page properties: " + e.getMessage()
            );
        }
    }
}