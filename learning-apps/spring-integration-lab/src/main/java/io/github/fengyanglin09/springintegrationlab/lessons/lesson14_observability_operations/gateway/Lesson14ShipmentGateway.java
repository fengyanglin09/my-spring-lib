package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ShipmentEvent;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ShipmentResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.support.Lesson14Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 14 shipment flow.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
@MessagingGateway
public interface Lesson14ShipmentGateway {

    // @Gateway says:
    // "When process(...) is called, send a message to lesson14ShipmentEvents."
    //
    // @Payload says:
    // "Use this method argument as the message payload."
    @Gateway(requestChannel = Lesson14Channels.SHIPMENT_EVENTS)
    Lesson14ShipmentResult process(@Payload Lesson14ShipmentEvent event);
}
