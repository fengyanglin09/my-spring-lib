package io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload after the transformer endpoint normalizes it.
 */
public record Lesson04NormalizedOrder(
        String orderId,
        String customerType,
        BigDecimal orderAmount,
        List<String> endpointTrail
) {
}
