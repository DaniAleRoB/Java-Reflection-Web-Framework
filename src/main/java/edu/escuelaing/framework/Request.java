package edu.escuelaing.framework;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP request received by the server.
 * Parses the HTTP method, path, and query parameters from the raw URI.
 */
public class Request {

    private String method;
    private String path;
    private Map<String, String> queryParams = new HashMap<>();

    public Request(String method, String uriStr) throws Exception {
        this.method = method;
        URI uri = new URI(uriStr);
        this.path = uri.getPath();
        if (uri.getQuery() != null) {
            parseQuery(uri.getQuery());
        }
    }

    private void parseQuery(String query) {
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key   = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = keyValue.length > 1
                    ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    : "";
            queryParams.put(key, value);
        }
    }

    /** Returns the HTTP method (GET, POST, etc.) */
    public String getMethod() {
        return method;
    }

    /** Returns the request path without query string */
    public String getPath() {
        return path;
    }

    /**
     * Returns the value of a query parameter by key.
     * Example: for ?name=Pedro, getValue("name") returns "Pedro"
     */
    public String getValue(String key) {
        return queryParams.get(key);
    }

    /**
     * Alias of getValue() — included to match framework contract.
     * Example usage: req.getValues("name")
     */
    public String getValues(String key) {
        return queryParams.get(key);
    }
}