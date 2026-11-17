package com.adobe.aem.guides.demo.core.servlets;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/demo/random",
        "sling.servlet.methods=GET"
    }
)
public class RandomServlet extends SlingSafeMethodsServlet {


    private static final long serialVersionUID = 1L;

    private static final String LETTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String type = request.getParameter("type");

        if (type == null || type.trim().isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Missing required parameter: type");
            return;
        }

        String result;

        switch (type) {
            case "Number":
                result = generateNumbers(6);
                break;

            case "Letters":
                result = generateLetters(6);
                break;

            case "Random":
                result = generateNumbers(3) + generateLetters(3);
                break;

            default:
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid type. Use Number, Letters, or Random.");
                return;
        }

        response.getWriter().write(result);
    }

    private String generateNumbers(int count) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < count; i++) {
            int digit = ThreadLocalRandom.current().nextInt(10);
            result.append(digit);
        }

        return result.toString();
    }

    private String generateLetters(int count) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < count; i++) {
            int index = ThreadLocalRandom.current()
                    .nextInt(LETTERS.length());

            result.append(LETTERS.charAt(index));
        }

        return result.toString();
    }

}
