package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/check-access")
public class AccessCheckServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG =
            LoggerFactory.getLogger(AccessCheckServlet.class);

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // Get logged-in user ID
        String userId = request.getResourceResolver().getUserID();

        try {

            // Get UserManager
            UserManager userManager = request.getResourceResolver()
                    .adaptTo(UserManager.class);

            if (userManager == null) {
                response.getWriter().write("Unable to access UserManager");
                return;
            }

            // Get logged-in user
            Authorizable user = userManager.getAuthorizable(userId);

            // Get Access group
            Authorizable groupAuthorizable =
                    userManager.getAuthorizable("Access");

            if (user == null || groupAuthorizable == null
                    || !(groupAuthorizable instanceof Group)) {

                response.getWriter().write(
                        "User or Access group not found");
                return;
            }

            Group accessGroup = (Group) groupAuthorizable;

            // Check whether user belongs to Access group
            if (accessGroup.isMember(user)) {

                LOG.info("User {} has access", userId);

                response.getWriter().write(
                        "User " + userId + " logged in has access");

            } else {

                LOG.info("User {} does not have access", userId);

                response.getWriter().write(
                        "User " + userId +
                        " logged in doesn't have access");
            }

        } catch (RepositoryException e) {

            LOG.error("Error checking group membership", e);

            response.getWriter().write(
                    "Error checking group membership");
        }
    }
}