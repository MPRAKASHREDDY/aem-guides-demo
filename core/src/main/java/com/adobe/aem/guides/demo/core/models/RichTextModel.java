package com.adobe.aem.guides.demo.core.models;


import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.xss.XSSAPI;

import javax.annotation.PostConstruct;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class RichTextModel {

    @ValueMapValue
    private String text;

    @ValueMapValue
    private boolean textIsRich;

    @OSGiService
    private XSSAPI xssApi;

    private String safeHtml;
    private String plainText;

    @PostConstruct
    protected void init() {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        if (textIsRich) {
            // keeps allowed formatting, removes scripts / unsafe attributes
            safeHtml = xssApi.filterHTML(text);
            // removes every tag, then unescapes common entities
            plainText = text.replaceAll("<[^>]*>", " ")
                    .replace("&nbsp;", " ")
                    .replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replaceAll("\\s+", " ")
                    .trim();
        } else {
            safeHtml = xssApi.encodeForHTML(text);
            plainText = text;
        }
    }

    public String getText() {
        return safeHtml;
    }

    public String getPlainText() {
        return plainText;
    }

    public boolean isEmpty() {
        return plainText == null || plainText.isEmpty();
    }
}