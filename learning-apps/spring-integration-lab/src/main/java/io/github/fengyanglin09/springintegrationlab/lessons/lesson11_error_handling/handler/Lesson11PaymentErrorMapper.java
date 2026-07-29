package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentAuthorizationException;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Converts error-channel exceptions into the lesson's normal reply type.
 */
// @Component tells Spring:
// "Create one Lesson11PaymentErrorMapper object during startup."
@Component
public class Lesson11PaymentErrorMapper {

    public Lesson11PaymentResult toFailureResult(Throwable throwable) {
        // The error flow calls this method when the gateway error channel
        // receives an ErrorMessage.
        //
        // Important vocabulary:
        //
        // An ErrorMessage is still a Spring Message. Its payload is a Throwable:
        // the exception that happened while the normal flow was handling the
        // original message.
        //
        // The .handle(Throwable.class, ...) step in the error flow gives this
        // method the ErrorMessage payload. That is why the parameter type here
        // is Throwable instead of ErrorMessage.
        //
        // The Throwable may contain two useful pieces of information:
        //
        // 1. Our business exception:
        //    Lesson11PaymentAuthorizationException
        //
        //    This tells us why the payment failed from the business point of
        //    view, such as MISSING_PAYMENT_TOKEN.
        //
        // 2. Spring's messaging exception:
        //    MessagingException
        //
        //    This can tell us which Spring Message was being handled when the
        //    failure happened. That failed Message contains the original
        //    payload and headers.
        Lesson11PaymentAuthorizationException paymentException = findPaymentException(throwable);
        Lesson11PaymentRequest failedRequest = findFailedRequest(throwable);

        String paymentId = failedRequest == null ? "unknown-payment" : failedRequest.paymentId();
        BigDecimal amount = failedRequest == null ? BigDecimal.ZERO : failedRequest.amount();
        String reasonCode = paymentException == null ? "UNKNOWN_PAYMENT_ERROR" : paymentException.reasonCode();

        return new Lesson11PaymentResult(
                paymentId,
                false,
                "DECLINED",
                reasonCode,
                amount,
                List.of(
                        "normal-flow:handler-threw-exception",
                        "gateway:error-channel-received-error-message",
                        "error-message:payload-is-throwable",
                        "error-flow:exception-to-failure-result"
                )
        );
    }

    private Lesson11PaymentAuthorizationException findPaymentException(Throwable throwable) {
        // Lesson11PaymentAuthorizationException is our business exception.
        //
        // It is thrown by Lesson11PaymentAuthorizer when the payment should not
        // be approved.
        //
        // It stores business information:
        //
        // - paymentId
        // - reasonCode
        // - exception message
        //
        // It does not store the whole Spring Message.
        //
        // Spring Integration may wrap this business exception inside another
        // exception, such as MessagingException, before sending it to the error
        // channel.
        //
        // This loop walks the cause chain until it finds our business exception.
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof Lesson11PaymentAuthorizationException paymentException) {
                return paymentException;
            }
            current = current.getCause();
        }
        return null;
    }

    private Lesson11PaymentRequest findFailedRequest(Throwable throwable) {
        // MessagingException is different from our business exception.
        //
        // Lesson11PaymentAuthorizationException says:
        // "The payment failed for this business reason."
        //
        // MessagingException says:
        // "A Spring Message failed while the messaging system was handling it."
        //
        // In this lesson, Spring Integration creates or wraps the failure in a
        // MessagingException so it can attach message context.
        //
        // That message context is stored in:
        //
        // messagingException.getFailedMessage()
        //
        // The failed message is the Spring Message being handled when the
        // exception happened. For our normal payment flow, that message usually
        // looks like this:
        //
        // Message
        //   payload = Lesson11PaymentRequest
        //   headers = message metadata
        //
        // That failed message is useful because it lets the error flow recover
        // the original request payload and headers that were present at the
        // failure point.
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof MessagingException messagingException) {
                // Ask Spring's MessagingException:
                // "Which Spring Message was being handled when the failure
                // happened?"
                Message<?> failedMessage = messagingException.getFailedMessage();

                // getPayload() may be a Lesson11PaymentRequest because the
                // original gateway call sent a Lesson11PaymentRequest into the
                // normal payment flow.
                //
                // We still check with instanceof because message payloads can
                // be any Java object in Spring Integration.
                if (failedMessage != null && failedMessage.getPayload() instanceof Lesson11PaymentRequest request) {
                    return request;
                }
            }
            current = current.getCause();
        }
        return null;
    }
}
