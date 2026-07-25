package io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.model;

import java.math.BigDecimal;

/**
 * The payload for lesson 02.
 *
 * <p>The payload is the main business data inside a Spring Integration message.
 * In this lesson, the business data is an order request.</p>
 */
public record Lesson02OrderRequest(
        String orderId,
        String customerId,
        BigDecimal orderAmount
) {
}
