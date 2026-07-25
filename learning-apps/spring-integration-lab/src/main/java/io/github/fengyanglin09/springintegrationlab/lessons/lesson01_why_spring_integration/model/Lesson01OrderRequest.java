package io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.model;

import java.math.BigDecimal;

/**
 * The payload that enters the lesson 01 flow.
 */
public record Lesson01OrderRequest(
        String orderId,
        String customerType,
        BigDecimal orderAmount
) {
}
