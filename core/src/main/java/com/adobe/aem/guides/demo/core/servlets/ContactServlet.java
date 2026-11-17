package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.demo.core.utills.ContactConstants;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=" + ContactConstants.SERVLET_PATH,
        "sling.servlet.methods=GET"
    }
)
public class ContactServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ContactServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

        String action = request.getParameter("action");
        ResourceResolver resolver = request.getResourceResolver();

        try {
            if ("create".equals(action)) {
                create(request, resolver, response);
            } else if ("update".equals(action)) {
                update(request, resolver, response);
            } else if ("delete".equals(action)) {
                delete(request, resolver, response);
            } else {
                reply(response, 400, "error", "Unknown action", null);
            }
        } catch (PersistenceException e) {
            resolver.revert();
            LOG.error("Contact action '{}' failed", action, e);
            reply(response, 500, "error", "Couldn't complete the request", null);
        }
    }

    private void create(SlingHttpServletRequest request, ResourceResolver resolver,
            SlingHttpServletResponse response) throws IOException, PersistenceException {

        String error = validate(request);
        if (error != null) {
            reply(response, 400, "error", error, null);
            return;
        }

        Map<String, Object> folder = new HashMap<>();
        folder.put("jcr:primaryType", "sling:Folder");
        Resource base = ResourceUtil.getOrCreateResource(
                resolver, ContactConstants.BASE_PATH, folder, "sling:Folder", false);

        String id = "contact-" + UUID.randomUUID();

        Map<String, Object> props = new HashMap<>();
        props.put("jcr:primaryType", "nt:unstructured");
        props.put("name", value(request, "name"));
        props.put("email", value(request, "email"));
        props.put("mobile", value(request, "mobile"));
        props.put("createdAt", Long.valueOf(System.currentTimeMillis()));

        resolver.create(base, id, props);
        resolver.commit();

        LOG.info("Created contact {}", id);
        reply(response, 200, "ok", "Contact saved", id);
    }

    private void update(SlingHttpServletRequest request, ResourceResolver resolver,
            SlingHttpServletResponse response) throws IOException, PersistenceException {

        String error = validate(request);
        if (error != null) {
            reply(response, 400, "error", error, null);
            return;
        }

        Resource contact = findContact(resolver, request.getParameter("id"));
        if (contact == null) {
            reply(response, 404, "error", "Contact not found", null);
            return;
        }

        ModifiableValueMap map = contact.adaptTo(ModifiableValueMap.class);
        if (map == null) {
            reply(response, 403, "error", "You can't edit this contact", null);
            return;
        }

        map.put("name", value(request, "name"));
        map.put("email", value(request, "email"));
        map.put("mobile", value(request, "mobile"));
        resolver.commit();

        LOG.info("Updated contact {}", contact.getName());
        reply(response, 200, "ok", "Contact updated", contact.getName());
    }

    private void delete(SlingHttpServletRequest request, ResourceResolver resolver,
            SlingHttpServletResponse response) throws IOException, PersistenceException {

        Resource contact = findContact(resolver, request.getParameter("id"));
        if (contact == null) {
            reply(response, 404, "error", "Contact not found", null);
            return;
        }

        String id = contact.getName();
        resolver.delete(contact);
        resolver.commit();

        LOG.info("Deleted contact {}", id);
        reply(response, 200, "ok", "Contact deleted", id);
    }

    private Resource findContact(ResourceResolver resolver, String id) {
        if (id == null || !ContactConstants.ID_PATTERN.matcher(id).matches()) {
            return null;
        }
        return resolver.getResource(ContactConstants.BASE_PATH + "/" + id);
    }

    private String validate(SlingHttpServletRequest request) {
        String name = value(request, "name");
        String email = value(request, "email");
        String mobile = value(request, "mobile");

        if (name.isEmpty() || name.length() > ContactConstants.MAX_LENGTH) {
            return "Enter a name";
        }
        if (email.length() > ContactConstants.MAX_LENGTH
                || !ContactConstants.EMAIL_PATTERN.matcher(email).matches()) {
            return "Enter a valid email";
        }
        if (!ContactConstants.MOBILE_PATTERN.matcher(mobile).matches()) {
            return "Enter a 10-digit mobile number";
        }
        return null;
    }

    private String value(SlingHttpServletRequest request, String key) {
        String v = request.getParameter(key);
        return v == null ? "" : v.trim();
    }

    // message and id are fixed strings or generated here, never user input, so they are JSON-safe
    private void reply(SlingHttpServletResponse response, int status, String result,
            String message, String id) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder();
        json.append("{\"status\":\"").append(result)
            .append("\",\"message\":\"").append(message).append("\"");
        if (id != null) {
            json.append(",\"id\":\"").append(id).append("\"");
        }
        json.append("}");
        response.getWriter().write(json.toString());
    }
}