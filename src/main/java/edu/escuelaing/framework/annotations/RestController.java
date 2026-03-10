package edu.escuelaing.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a REST controller (web component).
 * Classes annotated with @RestController are automatically discovered
 * by MicroSpringBoot via classpath scanning and their @GetMapping
 * methods are registered as REST routes.
 *
 * Usage:
 *   @RestController
 *   public class HelloController { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestController {
}
