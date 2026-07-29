package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.handler.Lesson11PaymentAuthorizer;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.handler.Lesson11PaymentErrorMapper;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.support.Lesson11Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson11PaymentFlows {

    // @Bean tells Spring:
    // "Create this normal payment IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson11PaymentIntegrationFlow(Lesson11PaymentAuthorizer authorizer) {
        // This is the normal path:
        //
        // payment request
        // -> call authorizer
        // -> return approved payment result
        //
        // If authorizer.authorize(...) throws an exception, this flow does not
        // catch it directly. The gateway decides whether that exception is:
        //
        // - thrown back to the caller
        // - sent to the lesson-specific error channel
        return IntegrationFlow.from(Lesson11Channels.PAYMENT_REQUESTS)
                // handle(...) creates a service-activator endpoint.
                //
                // Service activator means:
                // "Call ordinary application code to do work for this message."
                //
                // The typed lambda form says:
                //
                // - expect the payload to be Lesson11PaymentRequest
                // - call authorizer.authorize(request)
                // - use the returned Lesson11PaymentResult as the reply payload
                //
                // If authorize(...) throws, there is no normal reply from this
                // handler. The exception moves into the gateway's error-handling
                // path.
                .handle(Lesson11PaymentRequest.class, (request, headers) -> authorizer.authorize(request))
                .get();
    }

    // @Bean tells Spring:
    // "Create this error-handling IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson11PaymentErrorIntegrationFlow(Lesson11PaymentErrorMapper errorMapper) {
        // This is the error path for the recovering gateway:
        //
        // ErrorMessage
        // -> read the Throwable payload
        // -> map exception details to Lesson11PaymentResult
        // -> return declined payment result to the gateway caller
        return IntegrationFlow.from(Lesson11Channels.PAYMENT_ERRORS)
                // The message arriving on this channel is an ErrorMessage.
                //
                // ErrorMessage means:
                // "A Spring message whose payload is a Throwable."
                //
                // The typed lambda receives the payload of that ErrorMessage,
                // so the lambda parameter is Throwable, not ErrorMessage.
                //
                // errorMapper.toFailureResult(...) returns Lesson11PaymentResult.
                // That returned object becomes the reply payload sent back to
                // the gateway caller.
                .handle(Throwable.class, (throwable, headers) -> errorMapper.toFailureResult(throwable))
                .get();
    }
}
