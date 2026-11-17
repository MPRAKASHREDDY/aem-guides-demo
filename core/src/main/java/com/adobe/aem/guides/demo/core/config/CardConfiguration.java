
package com.adobe.aem.guides.demo.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Demo Card Configuration",
        description = "Configuration for the Demo Card component"
)
public @interface CardConfiguration {

    @AttributeDefinition(
            name = "Organization Name",
            description = "Name of the organization"
    )
    String getOrganizationName() default "ABC Technologies";

    @AttributeDefinition(
            name = "Homepage URL",
            description = "Organization homepage URL"
    )
    String getHomepageURL() default "https://www.example.com";
}
