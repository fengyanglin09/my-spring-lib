package io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.support.Lesson04Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "This class contains bean definitions that should be created during startup."
//
// In this lesson, the beans are the two in-memory channels used by the flow.
@Configuration
public class Lesson04ChannelConfiguration {

    // @Bean tells Spring:
    // "Call this method during startup and keep the returned object in the
    // application context."
    //
    // name = Lesson04Channels.RAW_ORDERS makes the channel bean's name exactly
    // "lesson04RawOrders". The gateway and flow use that same name to find this
    // channel.
    //
    // DirectChannel means:
    // "When code sends a message to this channel, immediately call the next
    // subscribed endpoint in the same thread."
    @Bean(name = Lesson04Channels.RAW_ORDERS)
    MessageChannel lesson04RawOrders() {
        return new DirectChannel();
    }

    // This second DirectChannel makes the middle of the lesson visible.
    //
    // The transformer endpoint sends its normalized order message here.
    // The service-activator endpoint receives that normalized order message
    // from this same channel.
    @Bean(name = Lesson04Channels.NORMALIZED_ORDERS)
    MessageChannel lesson04NormalizedOrders() {
        return new DirectChannel();
    }
}
