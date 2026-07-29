package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model;

import java.math.BigDecimal;

/**
 * Payment request payload sent into lesson 11.
 */
public record Lesson11PaymentRequest(
        String paymentId,
        String customerId,
        BigDecimal amount,
        boolean paymentTokenPresent
) {
}
