package io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model;

/**
 * JSON request body accepted by the POST quote endpoint.
 *
 * <p>The incoming JSON field names match the record component names:
 * destination, itemCount, and expedited. Spring MVC uses Jackson to turn the
 * request body into this record before the controller method is called.</p>
 */
public record Lesson07ShippingQuoteRequest(
        /*
         * Expected JSON:
         *
         * {
         *   "destination": "austin",
         *   "itemCount": 3,
         *   "expedited": true
         * }
         *
         * Jackson matches those JSON field names to these record component
         * names.
         */
        String destination,
        int itemCount,
        boolean expedited
) {
}
