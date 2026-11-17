
package com.adobe.aem.guides.demo.core.servlets;

import com.adobe.aem.guides.demo.core.config.CardConfiguration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = CardService.class,
        immediate = true
)
@Designate(ocd = CardConfiguration.class)
public class CardServiceImpl implements CardService {

    private static final Logger LOG =
            LoggerFactory.getLogger(CardServiceImpl.class);

    private CardConfiguration configuration;

    @Activate
    protected void activate(CardConfiguration configuration) {

        this.configuration = configuration;

        LOG.info("CardServiceImpl activated");
    }

    @Override
    public String getOrganizationName() {

        LOG.info("Reading organization name");

        return configuration.getOrganizationName();
    }

    @Override
    public String getHomepageURL() {

        LOG.info("Reading homepage URL");

        return configuration.getHomepageURL();
    }
}
