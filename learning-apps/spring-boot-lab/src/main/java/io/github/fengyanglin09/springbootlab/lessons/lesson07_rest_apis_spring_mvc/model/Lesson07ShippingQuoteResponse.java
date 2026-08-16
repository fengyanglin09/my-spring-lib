package io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model;

import java.math.BigDecimal;

/**
 * JSON response body returned after the API creates a shipping quote.
 *
 * <p>This is deliberately separate from Lesson07ShippingQuoteRequest. Request
 * and response models often differ: the client sends the input it knows, and
 * the server returns additional values such as quoteId, estimatedDays, cost,
 * and a message.</p>
 */
public record Lesson07ShippingQuoteResponse(
        /*
         * These fields are what the server sends back to the client after
         * creating the quote.
         *
         * Notice that quoteId, estimatedDays, estimatedCost, and message were
         * not part of the request. The service calculates them and the
         * controller returns them as JSON.
         */
        String quoteId,
        String destination,
        int itemCount,
        boolean expedited,
        int estimatedDays,
        BigDecimal estimatedCost,
        String message
) {
}
