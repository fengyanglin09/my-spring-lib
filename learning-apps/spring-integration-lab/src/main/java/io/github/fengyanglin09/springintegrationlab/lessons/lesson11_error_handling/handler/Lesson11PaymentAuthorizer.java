package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentAuthorizationException;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lesson-specific payment behavior called by the normal flow.
 */
// @Component tells Spring:
// "Create one Lesson11PaymentAuthorizer object during startup."
@Component
public class Lesson11PaymentAuthorizer {

    private static final BigDecimal REVIEW_LIMIT = new BigDecimal("500.00");

    public Lesson11PaymentResult authorize(Lesson11PaymentRequest request) {
        // This method is ordinary Java business logic.
        //
        // The flow calls it from a typed lambda. If this method returns a
        // Lesson11PaymentResult, the gateway receives that result as its normal
        // reply.
        //
        // If this method throws an exception, Spring Integration decides what
        // happens based on the gateway's error-channel configuration.
        if (!request.paymentTokenPresent()) {
            throw new Lesson11PaymentAuthorizationException(
                    request.paymentId(),
                    "MISSING_PAYMENT_TOKEN",
                    "Payment token is required"
            );
        }

        if (request.amount().compareTo(REVIEW_LIMIT) > 0) {
            throw new Lesson11PaymentAuthorizationException(
                    request.paymentId(),
                    "MANUAL_REVIEW_REQUIRED",
                    "Payments over 500.00 require manual review"
            );
        }

        return new Lesson11PaymentResult(
                request.paymentId(),
                true,
                "APPROVED",
                "NONE",
                request.amount(),
                List.of(
                        "normal-flow:received-payment-request",
                        "handler:authorized-payment",
                        "gateway:normal-reply"
                )
        );
    }
}
