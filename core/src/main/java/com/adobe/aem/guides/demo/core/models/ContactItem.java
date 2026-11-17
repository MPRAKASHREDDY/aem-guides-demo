package com.adobe.aem.guides.demo.core.models;

public class ContactItem {

    private final String id;
    private final String name;
    private final String email;
    private final String mobile;
    private final long createdAt;

    public ContactItem(String id, String name, String email, String mobile, long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}