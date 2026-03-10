package edu.escuelaing.framework;

import edu.escuelaing.framework.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the web framework components.
 */
class AppTest {

    // ─── Request tests ────────────────────────────────────────────────────────

    @Test
    void testRequestParsesPathCorrectly() throws Exception {
        Request req = new Request("GET", "/App/hello?name=Pedro");
        assertEquals("/App/hello", req.getPath());
    }

    @Test
    void testRequestParsesQueryParameter() throws Exception {
        Request req = new Request("GET", "/App/hello?name=Pedro");
        assertEquals("Pedro", req.getValue("name"));
    }

    @Test
    void testRequestGetValuesAlias() throws Exception {
        Request req = new Request("GET", "/App/hello?name=Juan");
        assertEquals("Juan", req.getValues("name"));
    }

    @Test
    void testRequestWithNoQueryReturnsNull() throws Exception {
        Request req = new Request("GET", "/App/pi");
        assertNull(req.getValue("name"));
    }

    @Test
    void testRequestParsesMethod() throws Exception {
        Request req = new Request("GET", "/App/pi");
        assertEquals("GET", req.getMethod());
    }

    @Test
    void testRequestMultipleQueryParams() throws Exception {
        Request req = new Request("GET", "/search?name=Pedro&age=30");
        assertEquals("Pedro", req.getValue("name"));
        assertEquals("30",    req.getValue("age"));
    }

    // ─── Response tests ───────────────────────────────────────────────────────

    @Test
    void testResponseDefaultStatus() {
        Response res = new Response();
        assertEquals(200, res.getStatus());
    }

    @Test
    void testResponseDefaultContentType() {
        Response res = new Response();
        assertEquals("text/html", res.getContentType());
    }

    @Test
    void testResponseSetStatus() {
        Response res = new Response();
        res.status(404);
        assertEquals(404, res.getStatus());
    }

    @Test
    void testResponseSetContentType() {
        Response res = new Response();
        res.type("application/json");
        assertEquals("application/json", res.getContentType());
    }

    // ─── RouteRegistry tests ──────────────────────────────────────────────────────────

    @Test
    void testRouteRegistryRegisterAndRetrieveRoute() {
        RouteRegistry.get("/test/route", (req, res) -> "test response");
        assertNotNull(RouteRegistry.getRoute("/test/route"));
    }

    @Test
    void testRouteRegistryUnknownRouteReturnsNull() {
        assertNull(RouteRegistry.getRoute("/this/does/not/exist"));
    }

    @Test
    void testRouteRegistryStaticfilesConfiguration() {
        RouteRegistry.staticfiles("/custom/folder");
        assertEquals("/custom/folder", RouteRegistry.getStaticFolder());
        RouteRegistry.staticfiles("/webroot"); // reset
    }

    // ─── Route lambda tests ───────────────────────────────────────────────────

    @Test
    void testRouteLambdaPiCorrectly() throws Exception {
        Route piRoute = (req, res) -> String.valueOf(Math.PI);
        Request req = new Request("GET", "/App/pi");
        Response res = new Response();
        assertEquals(String.valueOf(Math.PI), piRoute.handle(req, res));
    }

    @Test
    void testRouteLambdaHelloCorrectly() throws Exception {
        Route helloRoute = (req, res) -> "Hello " + req.getValue("name");
        Request req = new Request("GET", "/App/hello?name=Pedro");
        Response res = new Response();
        assertEquals("Hello Pedro", helloRoute.handle(req, res));
    }

    @Test
    void testRouteLambdaEulerCorrectly() throws Exception {
        Route eulerRoute = (req, res) -> String.valueOf(Math.E);
        Request req = new Request("GET", "/App/euler");
        Response res = new Response();
        assertEquals(String.valueOf(Math.E), eulerRoute.handle(req, res));
    }

    // ─── StaticFileHandler content type tests ─────────────────────────────────

    @Test
    void testContentTypeHtml() {
        assertEquals("text/html; charset=UTF-8",
                StaticFileHandler.getContentType("/index.html"));
    }

    @Test
    void testContentTypeCss() {
        assertEquals("text/css", StaticFileHandler.getContentType("/style.css"));
    }

    @Test
    void testContentTypeJs() {
        assertEquals("application/javascript",
                StaticFileHandler.getContentType("/app.js"));
    }

    @Test
    void testContentTypePng() {
        assertEquals("image/png", StaticFileHandler.getContentType("/logo.png"));
    }
}