package io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model.Lesson04EndpointReport;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model.Lesson04OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.support.Lesson04Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 04 endpoint flow.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
//
// The application can call this Java method instead of manually building a
// Message object and sending it to a channel.
@MessagingGateway
public interface Lesson04EndpointGateway {

    // @Gateway tells the generated gateway implementation:
    // "When process(...) is called, send the method argument as a message to
    // the channel named lesson04RawOrders."
    //
    // @Payload tells Spring Integration:
    // "Use this method argument as the message payload."
    //
    // The returned Lesson04EndpointReport comes from the final endpoint method
    // in the flow, Lesson04OrderReporter.report(...).
    //
    // The gateway knows this method returns Lesson04EndpointReport because the
    // Java method signature says so, and the flow must actually produce a
    // compatible reply object by the end of the request-reply flow.
    @Gateway(requestChannel = Lesson04Channels.RAW_ORDERS)
    Lesson04EndpointReport process(@Payload Lesson04OrderRequest request);
}
