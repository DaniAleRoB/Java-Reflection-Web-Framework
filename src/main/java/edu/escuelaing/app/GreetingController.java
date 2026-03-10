package edu.escuelaing.app;

import edu.escuelaing.framework.annotations.GetMapping;
import edu.escuelaing.framework.annotations.RequestParam;
import edu.escuelaing.framework.annotations.RestController;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Greeting controller — demonstrates @RequestParam with defaultValue.
 * This is the exact example from the taller specification.
 *
 * Endpoints:
 *   GET http://localhost:35000/greeting           → "Hola World"
 *   GET http://localhost:35000/greeting?name=Ana  → "Hola Ana"
 */
@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
