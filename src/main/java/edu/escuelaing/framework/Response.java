package edu.escuelaing.framework;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP response that the route handler can configure.
 * Holds the status code, content type, and custom headers.
 */
public class Response {

    private int status = 200;
    private String contentType = "text/html";
    private Map<String, String> headers = new HashMap<>();

    /** Sets the HTTP status code */
    public void status(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    /** Sets the Content-Type header */
    public void type(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    /** Adds a custom response header */
    public void header(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}