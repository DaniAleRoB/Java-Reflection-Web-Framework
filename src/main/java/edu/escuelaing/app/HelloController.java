package edu.escuelaing.app;

import edu.escuelaing.framework.annotations.GetMapping;
import edu.escuelaing.framework.annotations.RestController;

/**
 * Simple REST controller — demonstrates @RestController + @GetMapping
 * with no parameters.
 *
 * Endpoints:
 *   GET http://localhost:35000/       → "Greetings from Spring Boot!"
 *   GET http://localhost:35000/hello  → "Hello World!"
 */
@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "Greetings from MicroSpringBoot!";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
