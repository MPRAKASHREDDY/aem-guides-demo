package com.adobe.aem.guides.demo.core.services;

import com.adobe.aem.guides.demo.core.models.BlogArticle;
import java.util.List;
public interface BlogIndexService {

     /**
     * Fetches blog articles from the external EDS JSON index.
     *
     * @return list of blog articles
     */
    List<BlogArticle> getArticles();


}
