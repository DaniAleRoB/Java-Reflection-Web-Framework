package edu.escuelaing.framework;

import java.util.HashMap;
import java.util.Map;

/**
 * Core class of the web framework.
 * Provides static methods to register GET routes and configure static file location.
 *
 * Usage example:
 *   RouteRegistry.get("/App/hello", (req, res) -> "Hello " + req.getValue("name"));
 *   RouteRegistry.staticfiles("/webroot");
 */
public class RouteRegistry {

    private static final Map<String, Route> getRoutes = new HashMap<>();
    private static String staticFolder = "/webroot";

    /**
     * Registers a GET route with a lambda handler.
     *
     * @param path  the URL path (e.g. "/App/hello")
     * @param route the lambda that handles the request
     */
    public static void get(String path, Route route) {
        getRoutes.put(path, route);
    }

    /**
     * Returns the registered Route for the given path, or null if not found.
     */
    public static Route getRoute(String path) {
        return getRoutes.get(path);
    }

    /**
     * Sets the folder where static files are located.
     * The framework will look for files under target/classes + folder.
     *
     * @param folder path relative to target/classes (e.g. "/webroot")
     */
    public static void staticfiles(String folder) {
        staticFolder = folder;
    }

    /**
     * Returns the configured static files folder.
     */
    public static String getStaticFolder() {
        return staticFolder;
    }
}