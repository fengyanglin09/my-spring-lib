package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data shape used inside our application after the inbound adapter boundary.
 */
public record Lesson06InternalOrder(
        String orderId,
        String customerType,
        BigDecimal orderAmount,
        String shippingPriority,
        List<String> adapterTrail
) {
}
