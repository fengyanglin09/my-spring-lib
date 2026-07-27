package io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08RoutingResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.support.Lesson08Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 08 routing flow.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
@MessagingGateway
public interface Lesson08RoutingGateway {

    // @Gateway says:
    // "When route(...) is called, send the payload to lesson08OrderRequests."
    //
    // @Payload says:
    // "Use this method argument as the message payload."
    //
    // The returned Lesson08RoutingResult comes from either:
    //
    // - the filter discard flow, when the order is rejected
    // - one of the router branch subflows, when the order is accepted
    @Gateway(requestChannel = Lesson08Channels.ORDER_REQUESTS)
    Lesson08RoutingResult route(@Payload Lesson08OrderRequest request);
}
