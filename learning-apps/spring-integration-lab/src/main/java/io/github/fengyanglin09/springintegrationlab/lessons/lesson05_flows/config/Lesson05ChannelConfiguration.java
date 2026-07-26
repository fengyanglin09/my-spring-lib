package io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.support.Lesson05Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
//
// Lesson 05 uses two named channels so the flow reads like a diagram with
// visible labels.
@Configuration
public class Lesson05ChannelConfiguration {

    // @Bean tells Spring:
    // "Call this method during startup and keep the returned object."
    //
    // name = Lesson05Channels.ORDER_REQUESTS gives the channel this exact name:
    // "lesson05OrderRequests".
    //
    // The gateway sends new order messages to this channel. The flow starts
    // from this same channel.
    @Bean(name = Lesson05Channels.ORDER_REQUESTS)
    MessageChannel lesson05OrderRequests() {
        return new DirectChannel();
    }

    // This named channel is a checkpoint in the middle of the flow.
    //
    // The normalize step sends Lesson05NormalizedOrder messages here.
    // The assignHandlingLane step receives Lesson05NormalizedOrder messages
    // from here.
    @Bean(name = Lesson05Channels.NORMALIZED_ORDERS)
    MessageChannel lesson05NormalizedOrders() {
        return new DirectChannel();
    }
}
