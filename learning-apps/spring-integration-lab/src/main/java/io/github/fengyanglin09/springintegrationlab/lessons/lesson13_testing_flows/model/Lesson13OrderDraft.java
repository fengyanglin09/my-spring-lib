package io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model;

import java.math.BigDecimal;

/**
 * Raw order message used as the input payload for lesson 13.
 */
public record Lesson13OrderDraft(
        String orderId,
        String customerType,
        BigDecimal amount,
        boolean customerVerified
) {
}
