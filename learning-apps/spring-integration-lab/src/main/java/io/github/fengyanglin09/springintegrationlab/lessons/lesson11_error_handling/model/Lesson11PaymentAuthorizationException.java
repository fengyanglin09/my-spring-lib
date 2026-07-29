package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model;

/**
 * Business exception thrown when lesson 11 rejects a payment.
 */
public class Lesson11PaymentAuthorizationException extends RuntimeException {

    private final String paymentId;
    private final String reasonCode;

    public Lesson11PaymentAuthorizationException(String paymentId, String reasonCode, String message) {
        super(message);
        this.paymentId = paymentId;
        this.reasonCode = reasonCode;
    }

    public String paymentId() {
        return paymentId;
    }

    public String reasonCode() {
        return reasonCode;
    }
}
