
package com.adobe.aem.guides.demo.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    id = "com.adobe.aem.guides.demo.core.services.Task15ConfigServiceImpl",
    name = "Demo Task15 Configuration",
    description = "Configuration for Task 15"
)
public @interface Task15Config {

    @AttributeDefinition(
        name = "Client ID",
        description = "Enter the client ID"
    )
    String clientId() default "";

    @AttributeDefinition(
        name = "API Token",
        description = "Enter the API token"
    )
    String apiToken() default "";

    @AttributeDefinition(
        name = "Page Path",
        description = "Enter the AEM page path"
    )
    String pagePath() default "";
}
