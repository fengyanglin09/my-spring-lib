package io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload after the first flow step has cleaned up the raw request.
 */
public record Lesson05NormalizedOrder(
        String orderId,
        String customerType,
        BigDecimal orderAmount,
        boolean expedited,
        List<String> flowSteps
) {
}
