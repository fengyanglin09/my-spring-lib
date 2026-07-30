package io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Normalized order payload used after the transform step.
 */
public record Lesson13ReviewedOrder(
        String orderId,
        String normalizedCustomerType,
        BigDecimal amount,
        Lesson13ReviewDecision decision,
        List<String> reviewTrail
) {
}
