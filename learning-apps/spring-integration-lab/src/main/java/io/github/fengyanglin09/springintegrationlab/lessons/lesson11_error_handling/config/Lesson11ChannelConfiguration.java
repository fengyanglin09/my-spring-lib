package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.support.Lesson11Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson11ChannelConfiguration {

    // This is the normal request channel for lesson 11.
    //
    // Both lesson 11 gateways send Lesson11PaymentRequest payloads here. The
    // normal payment flow starts from this same channel.
    @Bean(name = Lesson11Channels.PAYMENT_REQUESTS)
    MessageChannel lesson11PaymentRequests() {
        return new DirectChannel();
    }

    // This is a lesson-specific error channel.
    //
    // Error channel means:
    // "If a gateway invocation fails and the gateway is configured with this
    // channel name, send an ErrorMessage here instead of throwing the exception
    // directly back to the caller."
    //
    // This is a DirectChannel so the error flow runs immediately in the same
    // gateway call and can return a normal Lesson11PaymentResult reply.
    @Bean(name = Lesson11Channels.PAYMENT_ERRORS)
    MessageChannel lesson11PaymentErrors() {
        return new DirectChannel();
    }
}
