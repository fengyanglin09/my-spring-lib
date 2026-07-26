package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.support.Lesson03Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "This class contains bean definitions."
@Configuration
public class Lesson03ChannelConfiguration {

    // @Bean(name = ...) creates a channel bean with the exact name used by the gateway and flow.
    //
    // DirectChannel means:
    // - one message goes to one subscriber
    // - it does not store messages for later
    // - it immediately calls the subscribed handler when a message is sent
    // - the handler runs in the same thread that sent the message
    @Bean(name = Lesson03Channels.DIRECT_ORDERS)
    MessageChannel lesson03DirectOrders() {
        return new DirectChannel();
    }

    // PublishSubscribeChannel means:
    // - one message is broadcast to every subscriber
    // - it is useful for event-style notifications
    // - with no executor configured, subscribers are invoked in the sender's thread
    @Bean(name = Lesson03Channels.BROADCAST_EVENTS)
    MessageChannel lesson03BroadcastEvents() {
        return new PublishSubscribeChannel();
    }
}
