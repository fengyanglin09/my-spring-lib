package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload after customer profile data has been added.
 */
public record Lesson07CustomerOrder(
        String orderId,
        String customerId,
        String customerTier,
        String region,
        BigDecimal orderAmount,
        String sku,
        List<String> shapeTrail
) {
}
