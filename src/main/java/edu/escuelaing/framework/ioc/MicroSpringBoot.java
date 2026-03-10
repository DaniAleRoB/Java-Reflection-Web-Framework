package edu.escuelaing.framework.ioc;

import edu.escuelaing.framework.annotations.GetMapping;
import edu.escuelaing.framework.annotations.RequestParam;
import edu.escuelaing.framework.annotations.RestController;
import edu.escuelaing.framework.RouteRegistry;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * IoC container / entry point for the MicroSpring framework.
 *
 * Two modes of operation:
 *
 *   1. Explicit class (lab day 1):
 *      java -cp target/classes edu.escuelaing.framework.ioc.MicroSpringBoot \
 *           edu.escuelaing.app.GreetingController
 *
 *   2. Classpath scan (final version) — no args:
 *      java -cp target/classes edu.escuelaing.framework.ioc.MicroSpringBoot
 *      Scans target/classes for all @RestController classes automatically.
 *
 * For each discovered @RestController the loader:
 *   - Instantiates the class via reflection (no-arg constructor)
 *   - Scans its methods for @GetMapping
 *   - Registers a lambda Route in RouteRegistry that:
 *       a) resolves @RequestParam parameters from the query string
 *       b) invokes the method via reflection
 *       c) returns the String result
 */
public class MicroSpringBoot {

    public static void main(String[] args) throws Exception {
        List<Class<?>> controllers = new ArrayList<>();

        if (args.length > 0) {
            // Mode 1: explicit class name passed as argument
            for (String className : args) {
                Class<?> cls = Class.forName(className);
                controllers.add(cls);
                System.out.println("[MicroSpringBoot] Loaded: " + className);
            }
        } else {
            // Mode 2: scan classpath for @RestController
            controllers = scanClasspath();
        }

        for (Class<?> controller : controllers) {
            if (!controller.isAnnotationPresent(RestController.class)) {
                System.out.println("[MicroSpringBoot] WARNING: "
                        + controller.getName() + " is not @RestController — skipping.");
                continue;
            }
            registerRoutes(controller);
        }

        // Start the HTTP server
        edu.escuelaing.framework.HttpServer.main(new String[]{});
    }

    /**
     * Instantiates the controller and registers one Route per @GetMapping method.
     */
    private static void registerRoutes(Class<?> controllerClass) throws Exception {
        Object instance = controllerClass.getDeclaredConstructor().newInstance();

        for (Method method : controllerClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(GetMapping.class)) continue;

            String path = method.getAnnotation(GetMapping.class).value();

            RouteRegistry.get(path, (req, res) -> {
                // Build argument array resolving @RequestParam annotations
                Object[] invokeArgs = resolveParams(method, req);
                Object result = method.invoke(instance, invokeArgs);
                return result != null ? result.toString() : "";
            });

            System.out.println("[MicroSpringBoot] Registered GET " + path
                    + " → " + controllerClass.getSimpleName() + "." + method.getName() + "()");
        }
    }

    /**
     * Resolves the arguments for a reflected method call by reading
     * @RequestParam annotations and matching them to query parameters.
     */
    private static Object[] resolveParams(Method method,
                                          edu.escuelaing.framework.Request req) {
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            if (params[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam rp = params[i].getAnnotation(RequestParam.class);
                String val = req.getValue(rp.value());
                args[i] = (val != null) ? val : rp.defaultValue();
            } else {
                args[i] = null;
            }
        }
        return args;
    }

    /**
     * Scans target/classes recursively for classes annotated with @RestController.
     */
    private static List<Class<?>> scanClasspath() throws Exception {
        List<Class<?>> found = new ArrayList<>();
        File classesDir = new File("target/classes");

        if (!classesDir.exists()) {
            System.err.println("[MicroSpringBoot] target/classes not found — run 'mvn compile' first.");
            return found;
        }

        URL[] urls = { classesDir.toURI().toURL() };
        URLClassLoader loader = new URLClassLoader(urls,
                MicroSpringBoot.class.getClassLoader());

        scanDir(classesDir, classesDir, loader, found);
        loader.close();
        System.out.println("[MicroSpringBoot] Classpath scan found " + found.size()
                + " @RestController(s).");
        return found;
    }

    private static void scanDir(File base, File dir,
                                 URLClassLoader loader,
                                 List<Class<?>> result) {
        File[] entries = dir.listFiles();
        if (entries == null) return;

        for (File entry : entries) {
            if (entry.isDirectory()) {
                scanDir(base, entry, loader, result);
            } else if (entry.getName().endsWith(".class")) {
                String relative = base.toURI().relativize(entry.toURI()).getPath();
                String className = relative
                        .replace('/', '.')
                        .replace('\\', '.')
                        .replace(".class", "");
                try {
                    Class<?> cls = loader.loadClass(className);
                    if (cls.isAnnotationPresent(RestController.class)) {
                        result.add(cls);
                        System.out.println("[MicroSpringBoot] Discovered: " + className);
                    }
                } catch (Throwable ignored) {
                    // skip unloadable classes (e.g. interfaces, inner anonymous)
                }
            }
        }
    }
}
