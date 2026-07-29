package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Reply returned by the lesson 11 payment gateways.
 */
public record Lesson11PaymentResult(
        String paymentId,
        boolean approved,
        String status,
        String reasonCode,
        BigDecimal amount,
        List<String> lessonTrail
) {
}
