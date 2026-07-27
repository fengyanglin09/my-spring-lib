package io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload after the flow has decided which route key should be used.
 */
public record Lesson08RoutableOrder(
        String orderId,
        String customerType,
        BigDecimal orderAmount,
        Lesson08RouteKey routeKey,
        List<String> routingTrail
) {
}
