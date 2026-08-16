package io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model;

/**
 * JSON response returned by the GET shipping option endpoint.
 *
 * <p>Records work well for simple API response shapes because they are compact,
 * immutable data carriers. Jackson can serialize record components into JSON
 * fields.</p>
 */
public record Lesson07ShippingOptionResponse(
        /*
         * These record components become JSON fields in the HTTP response.
         *
         * Example:
         *
         * {
         *   "destination": "austin",
         *   "supportsExpedited": true,
         *   "standardDays": 5,
         *   "expeditedDays": 2,
         *   "message": "Shipping options are available for austin"
         * }
         */
        String destination,
        boolean supportsExpedited,
        int standardDays,
        int expeditedDays,
        String message
) {
}
