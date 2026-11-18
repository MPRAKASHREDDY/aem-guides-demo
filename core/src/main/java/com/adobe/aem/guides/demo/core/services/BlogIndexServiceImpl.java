
package com.adobe.aem.guides.demo.core.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.demo.core.config.BlogIndexConfig;
import com.adobe.aem.guides.demo.core.models.BlogArticle;

@Component(service = BlogIndexService.class)
@Designate(ocd = BlogIndexConfig.class)
public class BlogIndexServiceImpl implements BlogIndexService {

    private static final Logger LOG =
            LoggerFactory.getLogger(BlogIndexServiceImpl.class);

    private String blogIndexUrl;
    private int connectionTimeout;
    private int readTimeout;

    @Activate
    protected void activate(BlogIndexConfig config) {

        blogIndexUrl = config.blogIndexUrl();
        connectionTimeout = config.connectionTimeout();
        readTimeout = config.readTimeout();

        LOG.info("Blog Index Service activated");
        LOG.info("Blog Index URL: {}", blogIndexUrl);
    }

    @Override
    public List<BlogArticle> getArticles() {

        HttpURLConnection connection = null;

        try {

            // STEP 1: Create URL
            URL url = new URL(blogIndexUrl);

            LOG.info("Connecting to EDS URL: {}", blogIndexUrl);

            // STEP 2: Open HTTP connection
            connection =
                    (HttpURLConnection) url.openConnection();

            // STEP 3: Configure HTTP request
            connection.setRequestMethod("GET");

            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            connection.setRequestProperty(
                    "Accept", "application/json");

            // STEP 4: Get HTTP response
            int responseCode = connection.getResponseCode();

            LOG.info("HTTP Response Code: {}", responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {

                LOG.error(
                        "Failed to retrieve blog JSON. HTTP Status: {}",
                        responseCode);

                return Collections.emptyList();
            }

            // STEP 5: Read JSON response
            StringBuilder response = new StringBuilder();

            try (InputStream inputStream =
                         connection.getInputStream();

                 BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         inputStream,
                                         StandardCharsets.UTF_8))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            LOG.info(
                    "JSON response received. Length: {}",
                    response.length());

            // STEP 6: Parse JSON response
            JSONObject jsonObject =
                    new JSONObject(response.toString());

            // STEP 7: Get data array
            JSONArray dataArray =
                    jsonObject.getJSONArray("data");

            LOG.info(
                    "Total blog articles received: {}",
                    dataArray.length());

            List<BlogArticle> articles =
                    new ArrayList<>();

            // STEP 8: Convert JSON objects into Java objects
            for (int i = 0; i < dataArray.length(); i++) {

                JSONObject articleJson =
                        dataArray.getJSONObject(i);

                BlogArticle article = new BlogArticle();

                article.setPath(
                        getJsonValue(articleJson, "path"));

                article.setLastModified(
                        getJsonValue(articleJson, "lastModified"));

                article.setTitle(
                        getJsonValue(articleJson, "title"));

                article.setDescription(
                        getJsonValue(articleJson, "description"));
 //ass
                article.setTags(
                        getJsonValue(articleJson, "tags"));

                article.setKeywords(
                        getJsonValue(articleJson, "keywords"));

                article.setImage(
                        getJsonValue(articleJson, "image"));

                article.setImageAlt(
                        getJsonValue(articleJson, "imageAlt"));

                article.setImagePath(
                        getJsonValue(articleJson, "imagePath"));

                article.setBlogPublishedDate(
                        getJsonValue(articleJson, "blogPublishedDate"));

                article.setBlogAuthor(
                        getJsonValue(articleJson, "blogAuthor"));

                article.setCategories(
                        getJsonValue(articleJson, "categories"));

                article.setBlogReadDuration(
                        getJsonValue(articleJson, "blogReadDuration"));

                LOG.info("---------- ARTICLE {} ----------", i + 1);
                LOG.info("Title: {}", article.getTitle());
                LOG.info("Path: {}", article.getPath());
                LOG.info("Description: {}", article.getDescription());
                LOG.info("Categories: {}", article.getCategories());
                LOG.info("Tags: {}", article.getTags());
                LOG.info("Author: {}", article.getBlogAuthor());
                LOG.info("Published Date: {}", article.getBlogPublishedDate());
                LOG.info("Image: {}", article.getDisplayImage());
                LOG.info("Read Duration: {}", article.getBlogReadDuration());

                articles.add(article);
            }

            LOG.info(
                    "Successfully converted {} articles into Java objects",
                    articles.size());

            // STEP 9: Return article list
            return articles;

        } catch (Exception e) {

            LOG.error(
                    "Error while fetching blog articles from EDS",
                    e);

            return Collections.emptyList();

        } finally {

            // STEP 10: Close HTTP connection
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Safely reads a JSON property as a String.
     */
    private String getJsonValue(
            JSONObject jsonObject, String propertyName)
            throws JSONException {

        if (!jsonObject.has(propertyName)
                || jsonObject.isNull(propertyName)) {

            return "";
        }

        Object value = jsonObject.get(propertyName);

        return String.valueOf(value);
    }
}
