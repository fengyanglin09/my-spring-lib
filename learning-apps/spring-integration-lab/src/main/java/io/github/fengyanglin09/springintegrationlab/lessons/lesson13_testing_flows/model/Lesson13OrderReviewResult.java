package io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Reply returned by the lesson 13 order review flow.
 */
public record Lesson13OrderReviewResult(
        String orderId,
        boolean accepted,
        String outcome,
        BigDecimal amount,
        List<String> reviewTrail
) {
}
