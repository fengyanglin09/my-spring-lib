package io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.support.Lesson13Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson13ChannelConfiguration {

    // @Bean tells Spring:
    // "Create a MessageChannel object during startup."
    //
    // name = Lesson13Channels.ORDER_REVIEW_REQUESTS means:
    // "Register this channel bean with the exact name lesson13OrderReviewRequests."
    //
    // The gateway sends Lesson13OrderDraft messages to this channel. The flow
    // starts from this same channel.
    @Bean(name = Lesson13Channels.ORDER_REVIEW_REQUESTS)
    MessageChannel lesson13OrderReviewRequests() {
        return new DirectChannel();
    }
}
