package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.support.Lesson14Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableMessageHistory;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
//
// @EnableMessageHistory tells Spring Integration:
// "Add a message-history header as messages pass through tracked components."
//
// The "lesson14*" pattern keeps this lesson focused. It asks Spring Integration
// to track components whose names start with lesson14 instead of tracking every
// component from every lesson in the application.
@Configuration
@EnableMessageHistory("lesson14*")
public class Lesson14ChannelConfiguration {

    // Normal input channel for shipment events.
    //
    // The shipment gateway sends Lesson14ShipmentEvent payloads here, and the
    // main shipment flow starts from this same channel.
    @Bean(name = Lesson14Channels.SHIPMENT_EVENTS)
    MessageChannel lesson14ShipmentEvents() {
        return new DirectChannel();
    }

    // Observation channel used by the wire tap.
    //
    // A wire tap sends a copy of the current message to this channel. The
    // original message still continues through the main shipment flow.
    @Bean(name = Lesson14Channels.OBSERVATION_EVENTS)
    MessageChannel lesson14ObservationEvents() {
        return new DirectChannel();
    }

    // Operation channel used by the control bus.
    //
    // Sending a command string to this channel lets the control bus call a
    // managed operation on a Spring bean.
    @Bean(name = Lesson14Channels.OPERATIONS)
    MessageChannel lesson14Operations() {
        return new DirectChannel();
    }
}
