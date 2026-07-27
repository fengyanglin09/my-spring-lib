package io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.support.Lesson08Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson08ChannelConfiguration {

    // This is the input channel for lesson 08.
    //
    // The gateway sends Lesson08OrderRequest payloads here. The routing flow
    // starts from this same channel.
    @Bean(name = Lesson08Channels.ORDER_REQUESTS)
    MessageChannel lesson08OrderRequests() {
        return new DirectChannel();
    }
}
