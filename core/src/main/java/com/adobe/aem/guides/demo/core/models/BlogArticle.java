
package com.adobe.aem.guides.demo.core.models;

public class BlogArticle {

    private String path;
    private String lastModified;
    private String title;
    private String description;
    private String tags;
    private String keywords;
    private String image;
    private String imageAlt;
    private String imagePath;
    private String blogPublishedDate;
    private String blogAuthor;
    private String categories;
    private String blogReadDuration;

    // Getters and Setters

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getBlogPublishedDate() {
        return blogPublishedDate;
    }

    public void setBlogPublishedDate(String blogPublishedDate) {
        this.blogPublishedDate = blogPublishedDate;
    }

    public String getBlogAuthor() {
        return blogAuthor;
    }

    public void setBlogAuthor(String blogAuthor) {
        this.blogAuthor = blogAuthor;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getBlogReadDuration() {
        return blogReadDuration;
    }

    public void setBlogReadDuration(String blogReadDuration) {
        this.blogReadDuration = blogReadDuration;
    }

    // Returns the article image
    public String getDisplayImage() {

        if (image != null && !image.trim().isEmpty()) {
            return image;
        }

        return imagePath;
    }

    // Returns the complete article URL
    public String getArticleUrl() {

        if (path == null || path.trim().isEmpty()) {
            return "#";
        }

        if (path.startsWith("https://")
                || path.startsWith("http://")) {
            return path;
        }

        return "https://www.lumen.com"
                + (path.startsWith("/") ? path : "/" + path);
    }
}
