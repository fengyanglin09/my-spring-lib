package io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Reply payload returned by the final endpoint in the flow.
 */
public record Lesson04EndpointReport(
        String orderId,
        String normalizedCustomerType,
        BigDecimal orderAmount,
        List<String> endpointTrail
) {
}
