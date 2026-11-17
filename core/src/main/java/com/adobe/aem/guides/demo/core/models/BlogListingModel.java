package com.adobe.aem.guides.demo.core.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.guides.demo.core.services.BlogIndexService;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class BlogListingModel {

    private static final Logger LOG =
            LoggerFactory.getLogger(BlogListingModel.class);

    private static final int DEFAULT_PAGE_SIZE = 12;

    @OSGiService
    private BlogIndexService blogIndexService;

    // Multiple categories selected in the AEM dialog
    @ValueMapValue
    private String[] contentTypeFilter;

    // Number of articles displayed initially and per Show More click
    @ValueMapValue
    private int maxItems;

    // All matching articles, sorted newest first
    private List<BlogArticle> articles = new ArrayList<>();

    // Number of cards shown initially and per click
    private int pageSize = DEFAULT_PAGE_SIZE;

    @PostConstruct
    protected void init() {

        LOG.info("BlogListingModel initialized");

        pageSize = maxItems > 0
                ? maxItems
                : DEFAULT_PAGE_SIZE;

        if (blogIndexService == null) {
            LOG.error("BlogIndexService is NULL");
            return;
        }

        LOG.info("BlogIndexService is available");

        List<BlogArticle> allArticles =
                blogIndexService.getArticles();

        LOG.info("Articles received from service: {}",
                allArticles != null ? allArticles.size() : "NULL");

        if (allArticles == null || allArticles.isEmpty()) {
            LOG.warn("No articles received from external JSON");
            return;
        }

        // Filter articles using selected categories
        for (BlogArticle article : allArticles) {

            boolean matchesContentType =
                    matchesAnyContentType(
                            article.getCategories(),
                            contentTypeFilter
                    );

            if (matchesContentType) {
                articles.add(article);
            }
        }

        LOG.info("Articles after category filtering: {}",
                articles.size());

        // Sort newest articles first
        articles.sort(Comparator.comparing(
                this::parsePublishedDate,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        LOG.info("Page size: {}, total articles: {}",
                pageSize, articles.size());
    }

    /**
     * Checks whether an article matches at least one
     * category selected by the author.
     */
    private boolean matchesAnyContentType(
            String articleCategories,
            String[] selectedCategories) {

        // No category selected means show all articles
        if (selectedCategories == null
                || selectedCategories.length == 0) {

            return true;
        }

        // Check every selected category
        for (String selectedCategory : selectedCategories) {

            if (matchesFilter(
                    articleCategories,
                    selectedCategory)) {

                return true;
            }
        }

        // No selected category matched
        return false;
    }

    /**
     * Checks whether the article category matches
     * the selected category value, handling AEM tag namespaces.
     */
    private boolean matchesFilter(
            String articleValue,
            String selectedFilter) {

        if (articleValue == null
                || selectedFilter == null
                || selectedFilter.trim().isEmpty()) {

            return false;
        }

        String cleanArticleVal = articleValue.trim().toLowerCase(Locale.ROOT);
        String cleanFilter = selectedFilter.trim().toLowerCase(Locale.ROOT);

        // AEM tag fields save tags with a namespace (e.g., "blog-categories:security-services").
        // Strip the namespace prefix so it cleanly compares with the article category.
        if (cleanFilter.contains(":")) {
            cleanFilter = cleanFilter.substring(cleanFilter.lastIndexOf(":") + 1);
        }

        return cleanArticleVal.contains(cleanFilter) || cleanFilter.contains(cleanArticleVal);
    }

    /**
     * Converts the published date into a Date object.
     */
    private Date parsePublishedDate(BlogArticle article) {

        String publishedDate = article.getBlogPublishedDate();

        if (publishedDate == null
                || publishedDate.trim().isEmpty()) {

            return null;
        }

        String[] patterns = {
                "MMMM d, yyyy",
                "MMM d, yyyy",
                "yyyy-MM-dd",
                "yyyy-MM-dd'T'HH:mm:ss'Z'"
        };

        for (String pattern : patterns) {

            SimpleDateFormat formatter =
                    new SimpleDateFormat(pattern, Locale.ENGLISH);

            formatter.setLenient(false);

            try {
                return formatter.parse(publishedDate.trim());
            } catch (ParseException e) {
                // Try the next date format
            }
        }

        return null;
    }

    // Return all matching articles to HTL
    public List<BlogArticle> getArticles() {
        return Collections.unmodifiableList(articles);
    }

    // Number of cards shown initially and per Show More click
    public int getPageSize() {
        return pageSize;
    }

    // Total number of matching articles
    public int getTotalCount() {
        return articles.size();
    }

    // True when more articles are available
    public boolean isHasMore() {
        return articles.size() > pageSize;
    }
}