package io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.model;

import java.math.BigDecimal;

/**
 * The reply payload returned by the lesson 02 flow.
 *
 * <p>This report proves that the handler can read both parts of a message:
 * the business payload and the metadata headers.</p>
 */
public record Lesson02MessageReport(
        String orderId,
        String customerId,
        BigDecimal orderAmount,
        String tenantId,
        String sourceSystem,
        String payloadType,
        boolean frameworkMessageIdPresent,
        boolean frameworkTimestampPresent
) {
}
