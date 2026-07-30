package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.support.Lesson14Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 14 control bus.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
@MessagingGateway
public interface Lesson14OperationsGateway {

    // @Gateway says:
    // "When operate(...) is called, send the command string to
    // lesson14Operations."
    //
    // The command string is not a business payload like a shipment event. It is
    // an operational instruction for the control bus, such as:
    //
    // lesson14ObservationRecorder.stopObservation
    @Gateway(requestChannel = Lesson14Channels.OPERATIONS)
    String operate(@Payload String command);
}
