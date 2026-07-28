package io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.support.Lesson10Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson10ChannelConfiguration {

    // QueueChannel is a Spring Integration channel that stores messages in an
    // internal queue until a consumer polls them.
    //
    // This is different from a DirectChannel:
    //
    // - DirectChannel hands a message to the next handler immediately.
    // - QueueChannel stores the message until someone calls receive(...), which
    //   is what a polling consumer does for us.
    //
    // The constructor argument 3 is the capacity.
    //
    // Capacity means:
    // "At most 3 messages can wait in this queue at the same time."
    //
    // We keep this number small so the backpressure behavior is easy to see in
    // the test. If a fourth message is submitted while the poller is stopped
    // and the queue already has 3 messages, that fourth send is rejected.
    //
    // This is real backpressure:
    // the producer learns that the system cannot accept more work right now.
    @Bean(name = Lesson10Channels.WORK_QUEUE)
    QueueChannel lesson10WorkQueue() {
        return new QueueChannel(3);
    }
}
