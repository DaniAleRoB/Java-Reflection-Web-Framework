package edu.escuelaing.framework;

import edu.escuelaing.app.GreetingController;
import edu.escuelaing.app.HelloController;
import edu.escuelaing.app.MathController;
import edu.escuelaing.framework.annotations.GetMapping;
import edu.escuelaing.framework.annotations.RequestParam;
import edu.escuelaing.framework.annotations.RestController;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the reflection / IoC layer:
 * annotation presence, route registration via reflection, and param resolution.
 */
class IoCTest {

    // ─── Annotation detection ──────────────────────────────────────────────

    @Test
    void testGreetingControllerHasRestControllerAnnotation() {
        assertTrue(GreetingController.class.isAnnotationPresent(RestController.class));
    }

    @Test
    void testHelloControllerHasRestControllerAnnotation() {
        assertTrue(HelloController.class.isAnnotationPresent(RestController.class));
    }

    @Test
    void testMathControllerHasRestControllerAnnotation() {
        assertTrue(MathController.class.isAnnotationPresent(RestController.class));
    }

    // ─── @GetMapping on methods ────────────────────────────────────────────

    @Test
    void testGreetingMethodHasGetMappingWithCorrectPath() throws Exception {
        Method m = GreetingController.class.getMethod("greeting", String.class);
        assertTrue(m.isAnnotationPresent(GetMapping.class));
        assertEquals("/greeting", m.getAnnotation(GetMapping.class).value());
    }

    @Test
    void testHelloIndexMethodHasGetMappingRoot() throws Exception {
        Method m = HelloController.class.getMethod("index");
        assertTrue(m.isAnnotationPresent(GetMapping.class));
        assertEquals("/", m.getAnnotation(GetMapping.class).value());
    }

    @Test
    void testMathPiMethodHasGetMapping() throws Exception {
        Method m = MathController.class.getMethod("pi");
        assertTrue(m.isAnnotationPresent(GetMapping.class));
        assertEquals("/pi", m.getAnnotation(GetMapping.class).value());
    }

    @Test
    void testMathSquareMethodHasGetMapping() throws Exception {
        Method m = MathController.class.getMethod("square", String.class);
        assertTrue(m.isAnnotationPresent(GetMapping.class));
        assertEquals("/square", m.getAnnotation(GetMapping.class).value());
    }

    // ─── @RequestParam resolution ──────────────────────────────────────────

    @Test
    void testGreetingParamHasRequestParamAnnotation() throws Exception {
        Method m = GreetingController.class.getMethod("greeting", String.class);
        Parameter p = m.getParameters()[0];
        assertTrue(p.isAnnotationPresent(RequestParam.class));
    }

    @Test
    void testGreetingParamNameIsName() throws Exception {
        Method m = GreetingController.class.getMethod("greeting", String.class);
        RequestParam rp = m.getParameters()[0].getAnnotation(RequestParam.class);
        assertEquals("name", rp.value());
    }

    @Test
    void testGreetingParamDefaultValueIsWorld() throws Exception {
        Method m = GreetingController.class.getMethod("greeting", String.class);
        RequestParam rp = m.getParameters()[0].getAnnotation(RequestParam.class);
        assertEquals("World", rp.defaultValue());
    }

    @Test
    void testSquareParamDefaultValueIsZero() throws Exception {
        Method m = MathController.class.getMethod("square", String.class);
        RequestParam rp = m.getParameters()[0].getAnnotation(RequestParam.class);
        assertEquals("0", rp.defaultValue());
    }

    // ─── Reflection invocation ─────────────────────────────────────────────

    @Test
    void testGreetingInvocationWithName() throws Exception {
        GreetingController ctrl = new GreetingController();
        Method m = GreetingController.class.getMethod("greeting", String.class);
        String result = (String) m.invoke(ctrl, "Pedro");
        assertEquals("Hola Pedro", result);
    }

    @Test
    void testGreetingInvocationDefaultValue() throws Exception {
        GreetingController ctrl = new GreetingController();
        Method m = GreetingController.class.getMethod("greeting", String.class);
        // simulate no param → use defaultValue "World"
        String result = (String) m.invoke(ctrl, "World");
        assertEquals("Hola World", result);
    }

    @Test
    void testHelloIndexInvocation() throws Exception {
        HelloController ctrl = new HelloController();
        Method m = HelloController.class.getMethod("index");
        String result = (String) m.invoke(ctrl);
        assertEquals("Greetings from MicroSpringBoot!", result);
    }

    @Test
    void testMathPiInvocation() throws Exception {
        MathController ctrl = new MathController();
        Method m = MathController.class.getMethod("pi");
        String result = (String) m.invoke(ctrl);
        assertEquals(String.valueOf(Math.PI), result);
    }

    @Test
    void testMathSquareInvocation() throws Exception {
        MathController ctrl = new MathController();
        Method m = MathController.class.getMethod("square", String.class);
        assertEquals("25.0", m.invoke(ctrl, "5"));
        assertEquals("0.0",  m.invoke(ctrl, "0"));
    }

    @Test
    void testMathSquareBadInput() throws Exception {
        MathController ctrl = new MathController();
        Method m = MathController.class.getMethod("square", String.class);
        String result = (String) m.invoke(ctrl, "abc");
        assertTrue(result.startsWith("Error:"));
    }

    // ─── RouteRegistry route registration via reflection ───────────────────────────

    @Test
    void testRouteRegistryRouteRegisteredForGreeting() throws Exception {
        // Simulate what MicroSpringBoot does
        GreetingController ctrl = new GreetingController();
        Method m = GreetingController.class.getMethod("greeting", String.class);
        String path = m.getAnnotation(GetMapping.class).value();

        RouteRegistry.get(path, (req, res) -> {
            String name = req.getValue("name");
            if (name == null) name = m.getParameters()[0].getAnnotation(RequestParam.class).defaultValue();
            return (String) m.invoke(ctrl, name);
        });

        assertNotNull(RouteRegistry.getRoute("/greeting"));
    }

    @Test
    void testRouteRegistryRouteForGreetingReturnsCorrectValue() throws Exception {
        GreetingController ctrl = new GreetingController();
        Method m = GreetingController.class.getMethod("greeting", String.class);

        RouteRegistry.get("/greeting-test", (req, res) -> (String) m.invoke(ctrl, "Ana"));

        Route route = RouteRegistry.getRoute("/greeting-test");
        assertNotNull(route);

        Request req = new Request("GET", "/greeting-test?name=Ana");
        Response res = new Response();
        assertEquals("Hola Ana", route.handle(req, res));
    }
}
