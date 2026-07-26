package io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model.Lesson05FlowResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model.Lesson05OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.support.Lesson05Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 05 flow.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
//
// Your code can call prepare(...) like a normal Java method, while Spring
// Integration turns that call into a message sent to a channel.
@MessagingGateway
public interface Lesson05FlowGateway {

    // @Gateway says:
    // "When prepare(...) is called, send the payload to lesson05OrderRequests."
    //
    // @Payload says:
    // "Use this method argument as the message payload."
    //
    // Lesson05FlowResult is returned because the final step in the flow returns
    // a Lesson05FlowResult object.
    @Gateway(requestChannel = Lesson05Channels.ORDER_REQUESTS)
    Lesson05FlowResult prepare(@Payload Lesson05OrderRequest request);
}
