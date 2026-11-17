package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;



@Component(
    service=Servlet.class,
    property={
        "sling.servlet.paths=/bin/prakash",
        "sling.servlet.methods=GET"
    }
)
public class PathBaseServlet extends SlingSafeMethodsServlet {

    @Override 
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.getWriter().write("Hello from PathBaseServlet");
    }
  
}