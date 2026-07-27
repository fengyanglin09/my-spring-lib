package io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Reply returned by the lesson 08 gateway.
 */
public record Lesson08RoutingResult(
        String orderId,
        boolean accepted,
        String path,
        BigDecimal orderAmount,
        List<String> routingTrail
) {
}
