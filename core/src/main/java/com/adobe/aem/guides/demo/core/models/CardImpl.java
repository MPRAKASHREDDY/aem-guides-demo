
package com.adobe.aem.guides.demo.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.demo.core.servlets.CardService;

@Model(
        adaptables = Resource.class,
        adapters = Card.class,
        resourceType = CardImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CardImpl implements Card {

    protected static final String RESOURCE_TYPE =
            "demo/components/content/card";

    private static final Logger LOG =
            LoggerFactory.getLogger(CardImpl.class);

    @ValueMapValue
    private String cardTitle;

    @ValueMapValue
    private String buttonText;

    @OSGiService
    private CardService cardService;

    private String organizationName;

    private String homepageURL;

    @PostConstruct
    protected void init() {

        if (cardService == null) {
            LOG.error("CardService is not available");
            return;
        }

        organizationName = cardService.getOrganizationName();

        homepageURL = cardService.getHomepageURL();

        LOG.info("Organization Name: {}", organizationName);

        LOG.info("Homepage URL: {}", homepageURL);
    }

    @Override
    public String getCardTitle() {
        return cardTitle;
    }

    @Override
    public String getButtonText() {
        return buttonText;
    }

    @Override
    public String getOrganizationName() {
        return organizationName;
    }

    @Override
    public String getHomepageURL() {
        return homepageURL;
    }
}
