package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.support.Lesson11Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Gateway that converts downstream exceptions into payment failure replies.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
//
// errorChannel says:
// "If a downstream handler throws during this gateway call, send an
// ErrorMessage to lesson11PaymentErrors."
//
// Because the error flow returns Lesson11PaymentResult, the caller receives a
// normal declined result instead of catching an exception.
@MessagingGateway(errorChannel = Lesson11Channels.PAYMENT_ERRORS)
public interface Lesson11RecoveringPaymentGateway {

    // @Gateway says:
    // "When authorize(...) is called, send the payload to lesson11PaymentRequests."
    //
    // @Payload says:
    // "Use this method argument as the message payload."
    @Gateway(requestChannel = Lesson11Channels.PAYMENT_REQUESTS)
    Lesson11PaymentResult authorize(@Payload Lesson11PaymentRequest request);
}
