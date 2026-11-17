package com.adobe.aem.guides.demo.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
@ObjectClassDefinition(
        name = "Demo Blog Index Configuration",
        description = "Configuration for fetching external EDS blog articles"
)
public @interface BlogIndexConfig {

    @AttributeDefinition(
            name = "Blog Index URL",
            description = "External EDS blog JSON index URL"
    )
    String blogIndexUrl() default
            "https://www.lumen.com/blog/query-index.json";

    @AttributeDefinition(
            name = "Connection Timeout",
            description = "HTTP connection timeout in milliseconds"
    )
    int connectionTimeout() default 10000;

    @AttributeDefinition(
            name = "Read Timeout",
            description = "HTTP response read timeout in milliseconds"
    )
    int readTimeout() default 15000;

}
