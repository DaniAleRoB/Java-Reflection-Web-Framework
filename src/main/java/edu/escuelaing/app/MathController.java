package edu.escuelaing.app;

import edu.escuelaing.framework.annotations.GetMapping;
import edu.escuelaing.framework.annotations.RequestParam;
import edu.escuelaing.framework.annotations.RestController;

/**
 * Math controller — demonstrates multiple @GetMapping endpoints
 * and @RequestParam with numeric operations.
 *
 * Endpoints:
 *   GET http://localhost:35000/pi                     → "3.141592653589793"
 *   GET http://localhost:35000/euler                  → "2.718281828459045"
 *   GET http://localhost:35000/square?value=5         → "25.0"
 */
@RestController
public class MathController {

    @GetMapping("/pi")
    public String pi() {
        return String.valueOf(Math.PI);
    }

    @GetMapping("/euler")
    public String euler() {
        return String.valueOf(Math.E);
    }

    @GetMapping("/square")
    public String square(@RequestParam(value = "value", defaultValue = "0") String value) {
        try {
            double n = Double.parseDouble(value);
            return String.valueOf(n * n);
        } catch (NumberFormatException e) {
            return "Error: 'value' must be a number";
        }
    }
}
