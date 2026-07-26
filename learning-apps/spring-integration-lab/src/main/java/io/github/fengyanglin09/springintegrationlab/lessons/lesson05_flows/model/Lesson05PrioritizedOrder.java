package io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload after the flow decides which handling lane should process the order.
 */
public record Lesson05PrioritizedOrder(
        String orderId,
        String customerType,
        BigDecimal orderAmount,
        String handlingLane,
        List<String> flowSteps
) {
}
