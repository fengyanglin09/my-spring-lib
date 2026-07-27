package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload after the raw CSV text has been parsed.
 */
public record Lesson07OrderDraft(
        String orderId,
        String customerId,
        BigDecimal orderAmount,
        String sku,
        List<String> shapeTrail
) {
}
