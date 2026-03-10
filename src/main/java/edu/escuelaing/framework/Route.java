package edu.escuelaing.framework;

/**
 * Functional interface representing a REST route handler.
 * Implementations are typically provided as lambda expressions.
 *
 * Example:
 *   get("/App/hello", (req, res) -> "Hello " + req.getValue("name"));
 */
@FunctionalInterface
public interface Route {
    String handle(Request req, Response res) throws Exception;
}