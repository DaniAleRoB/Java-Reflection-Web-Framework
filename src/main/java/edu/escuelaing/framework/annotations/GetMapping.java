package edu.escuelaing.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an HTTP GET request to a specific path.
 * Applied to methods inside a @RestController class.
 * The method must return a String.
 *
 * Usage:
 *   @GetMapping("/hello")
 *   public String hello() { return "Hello!"; }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    String value();
}
