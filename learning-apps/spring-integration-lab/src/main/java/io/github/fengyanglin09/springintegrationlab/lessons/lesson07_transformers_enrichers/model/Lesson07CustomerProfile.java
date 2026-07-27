package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model;

/**
 * Extra customer data used to enrich an order payload.
 */
public record Lesson07CustomerProfile(
        String customerId,
        String customerTier,
        String region
) {
}
