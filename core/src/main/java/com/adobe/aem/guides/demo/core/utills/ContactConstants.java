package com.adobe.aem.guides.demo.core.utills;

import java.util.regex.Pattern;

public final class ContactConstants {

    public static final String BASE_PATH = "/var/demo/contacts";
    public static final String SERVLET_PATH = "/bin/demo/contact";
    public static final int MAX_LENGTH = 100;

    public static final Pattern ID_PATTERN = Pattern.compile("^contact-[a-zA-Z0-9-]{1,50}$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    public static final Pattern MOBILE_PATTERN = Pattern.compile("^\\d{10}$");

    private ContactConstants() {
    }
}