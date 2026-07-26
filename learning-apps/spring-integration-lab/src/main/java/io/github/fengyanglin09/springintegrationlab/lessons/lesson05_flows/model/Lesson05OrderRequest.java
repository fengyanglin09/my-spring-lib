package io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model;

import java.math.BigDecimal;

/**
 * Raw order payload entering the lesson 05 flow.
 */
public record Lesson05OrderRequest(
        String orderId,
        String customerType,
        BigDecimal orderAmount,
        boolean expedited
) {
}
