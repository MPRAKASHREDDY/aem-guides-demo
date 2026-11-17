package com.adobe.aem.guides.demo.core.services;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Task15UpdateService.class)
public class Task15UpdateServiceImpl implements Task15UpdateService {

    private static final String SUBSERVICE = "data-writer";
    private static final Logger LOG =
            LoggerFactory.getLogger(Task15UpdateServiceImpl.class);
    @Reference
    private Task15ConfigService configService;

    @Reference
    private ResourceResolverFactory resolverFactory;

    // Scheduler calls this method.
    // It gets clientId and apiToken from OSGi configuration.
    @Override
    public void updatePageProperties()
            throws LoginException, PersistenceException {

        String clientId = configService.getClientId();
        String apiToken = configService.getApiToken();

         LOG.info(
               clientId + " " + apiToken
            );

        
        updatePageProperties(clientId, apiToken);
    }

    // Servlet calls this method.
    // It receives clientId and apiToken from Postman.
    @Override
    public void updatePageProperties(String clientId, String apiToken)
            throws LoginException, PersistenceException {

        String pagePath = configService.getPagePath();

        if (pagePath == null || pagePath.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Page path is not configured in OSGi configuration"
            );
        }

        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("clientId is required");
        }

        if (apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalArgumentException("apiToken is required");
        }

        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(
            ResourceResolverFactory.SUBSERVICE,
            SUBSERVICE
        );

        try (ResourceResolver resolver =
                resolverFactory.getServiceResourceResolver(authInfo)) {

            Resource pageResource = resolver.getResource(pagePath);

            if (pageResource == null) {
                throw new IllegalArgumentException(
                    "Invalid page path: " + pagePath
                );
            }
             LOG.info("jcr content found: {}", pageResource.getPath());
            Resource contentResource =
                resolver.getResource(pagePath + "/jcr:content");

            if (contentResource == null) {
                throw new IllegalArgumentException(
                    "jcr:content node not found"
                );
            }

            ModifiableValueMap properties =
                contentResource.adaptTo(ModifiableValueMap.class);

            if (properties == null) {
                throw new IllegalStateException(
                    "Unable to modify page properties"
                );
            }
            LOG.info("jcr:content found: {}", contentResource.getPath());
            

            properties.put("clientId", clientId);
            properties.put("apiToken", apiToken);

            resolver.commit();
            LOG.info("Properties successfully saved at: {}",
        contentResource.getPath());

        }
    }
}