package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model.Lesson03DeliveryRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model.Lesson03DirectDeliveryReport;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.support.Lesson03Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point for sending messages to lesson 03 channels.
 */
// @MessagingGateway tells Spring Integration:
// "Create a Spring bean that implements this interface for me."
@MessagingGateway
public interface Lesson03ChannelGateway {

    // This method sends a request message to a DirectChannel.
    //
    // Since a DirectChannel sends to one handler and that handler returns a value,
    // this gateway method can wait for and return Lesson03DirectDeliveryReport.
    @Gateway(requestChannel = Lesson03Channels.DIRECT_ORDERS)
    Lesson03DirectDeliveryReport sendDirect(@Payload Lesson03DeliveryRequest request);

    // This method sends an event message to a PublishSubscribeChannel.
    //
    // The method returns void because a broadcast event has multiple subscribers.
    // There is no single handler reply that should become this method's return value.
    @Gateway(requestChannel = Lesson03Channels.BROADCAST_EVENTS)
    void broadcast(@Payload Lesson03DeliveryRequest request);
}
