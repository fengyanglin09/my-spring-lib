package io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model;

import java.math.BigDecimal;

/**
 * Raw order payload entering the lesson 04 flow.
 */
public record Lesson04OrderRequest(
        String orderId,
        String customerType,
        BigDecimal orderAmount
) {
}
