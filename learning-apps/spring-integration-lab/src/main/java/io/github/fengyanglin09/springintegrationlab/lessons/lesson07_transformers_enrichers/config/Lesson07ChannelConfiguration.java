package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.support.Lesson07Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson07ChannelConfiguration {

    // This is the input channel for raw CSV order lines.
    //
    // The gateway sends the raw String payload here. The lesson 07 flow starts
    // from this same channel.
    @Bean(name = Lesson07Channels.RAW_ORDER_LINES)
    MessageChannel lesson07RawOrderLines() {
        return new DirectChannel();
    }
}
