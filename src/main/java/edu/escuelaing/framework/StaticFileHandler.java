package edu.escuelaing.framework;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Handles serving static files (HTML, CSS, JS, images) from the configured folder.
 * Files are looked up under: target/classes + staticFolder + requestPath
 */
public class StaticFileHandler {

    /**
     * Attempts to read and return the bytes of the requested static file.
     *
     * @param path the URL path (e.g. "/index.html")
     * @return file bytes if found, or null if the file does not exist
     */
    public static byte[] getFile(String path) throws IOException {
        String baseDir = "target/classes" + RouteRegistry.getStaticFolder();
        File file = new File(baseDir + path);

        if (file.exists() && !file.isDirectory()) {
            return Files.readAllBytes(file.toPath());
        }
        return null;
    }

    /**
     * Returns the appropriate MIME Content-Type based on file extension.
     *
     * @param path the file path or URL
     * @return MIME type string
     */
    public static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".js"))   return "application/javascript";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif"))  return "image/gif";
        if (path.endsWith(".ico"))  return "image/x-icon";
        if (path.endsWith(".json")) return "application/json";
        return "text/plain";
    }
}