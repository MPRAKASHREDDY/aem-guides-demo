package com.adobe.aem.guides.demo.core.services;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;


public interface Task15UpdateService {

    // Used by scheduler: reads values from OSGi configuration
    void updatePageProperties()
            throws LoginException, PersistenceException;

    // Used by servlet: receives values from Postman
    void updatePageProperties(String clientId, String apiToken)
            throws LoginException, PersistenceException;
}