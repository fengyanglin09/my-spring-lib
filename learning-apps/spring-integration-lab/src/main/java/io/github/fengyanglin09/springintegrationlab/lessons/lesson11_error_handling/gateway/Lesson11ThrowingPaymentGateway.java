package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.support.Lesson11Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Gateway that uses the default behavior: downstream exceptions are thrown.
 */
// This gateway intentionally has no errorChannel.
//
// Default behavior:
// If the normal flow throws an exception, the gateway call fails and the caller
// must handle the exception.
@MessagingGateway
public interface Lesson11ThrowingPaymentGateway {

    @Gateway(requestChannel = Lesson11Channels.PAYMENT_REQUESTS)
    Lesson11PaymentResult authorize(@Payload Lesson11PaymentRequest request);
}
