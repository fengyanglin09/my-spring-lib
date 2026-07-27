package io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model;

import java.math.BigDecimal;

/**
 * Raw payload entering the lesson 08 routing flow.
 */
public record Lesson08OrderRequest(
        String orderId,
        String customerType,
        BigDecimal orderAmount,
        boolean expedited,
        boolean customerVerified
) {
}
