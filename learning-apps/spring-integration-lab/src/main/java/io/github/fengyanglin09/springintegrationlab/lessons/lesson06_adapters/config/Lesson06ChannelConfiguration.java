package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support.Lesson06Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson06ChannelConfiguration {

    // This named channel is just inside the application boundary.
    //
    // The inbound adapter gets a plain external record from Lesson06ExternalOrderInbox,
    // wraps it as a Spring Integration message, and sends that message here.
    @Bean(name = Lesson06Channels.EXTERNAL_ORDER_RECORDS)
    MessageChannel lesson06ExternalOrderRecords() {
        return new DirectChannel();
    }

    // This named channel is just before the outbound boundary.
    //
    // Internal flow steps prepare a Lesson06PartnerOrderRequest and send it
    // here. The outbound adapter receives from this channel and calls the
    // simulated partner system.
    @Bean(name = Lesson06Channels.PARTNER_ORDER_REQUESTS)
    MessageChannel lesson06PartnerOrderRequests() {
        return new DirectChannel();
    }
}
