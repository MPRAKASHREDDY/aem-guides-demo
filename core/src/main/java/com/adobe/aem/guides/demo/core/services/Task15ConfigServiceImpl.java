package com.adobe.aem.guides.demo.core.services;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.aem.guides.demo.core.config.Task15Config;

@Component(
    service = Task15ConfigService.class,
    immediate = true,
    configurationPid = "com.adobe.aem.guides.demo.core.services.Task15ConfigServiceImpl"
)
@Designate(ocd = Task15Config.class)
public class Task15ConfigServiceImpl implements Task15ConfigService {

    private String clientId;
    private String apiToken;
    private String pagePath;
     private static final Logger LOG =
            LoggerFactory.getLogger(Task15ConfigServiceImpl.class);

    @Activate
    protected void activate(Task15Config config) {

        clientId = config.clientId();
        apiToken = config.apiToken();
        pagePath = config.pagePath();

        LOG.info("Task15 Configuration Activated");
    LOG.info("Client ID: {}", clientId);
    LOG.info("API Token: {}", apiToken);
    LOG.info("Page Path: {}", pagePath);
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getApiToken() {
        return apiToken;
    }

    @Override
    public String getPagePath() {
        return pagePath;
    }
}