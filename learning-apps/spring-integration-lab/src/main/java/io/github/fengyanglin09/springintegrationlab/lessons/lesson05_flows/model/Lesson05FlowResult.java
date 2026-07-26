package io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Final reply payload returned to the gateway caller.
 */
public record Lesson05FlowResult(
        String orderId,
        String customerType,
        BigDecimal orderAmount,
        String handlingLane,
        List<String> flowSteps
) {
}
