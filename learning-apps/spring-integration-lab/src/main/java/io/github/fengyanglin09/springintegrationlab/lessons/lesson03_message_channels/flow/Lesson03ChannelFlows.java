package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.handler.Lesson03BroadcastRecorder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.handler.Lesson03DirectOrderHandler;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.support.Lesson03Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
public class Lesson03ChannelFlows {

    @Bean
    IntegrationFlow lesson03DirectFlow(Lesson03DirectOrderHandler handler) {
        return IntegrationFlow.from(Lesson03Channels.DIRECT_ORDERS)
                // This DirectChannel has one subscriber: the direct order handler.
                // The handler return value becomes the reply to the gateway call.
                .handle(handler, "handle")
                .get();
    }

    @Bean
    IntegrationFlow lesson03AuditBroadcastFlow(Lesson03BroadcastRecorder recorder) {
        return IntegrationFlow.from(Lesson03Channels.BROADCAST_EVENTS)
                // This is the first subscriber to the PublishSubscribeChannel.
                .handle(recorder, "recordAudit")
                .get();
    }

    @Bean
    IntegrationFlow lesson03NotificationBroadcastFlow(Lesson03BroadcastRecorder recorder) {
        return IntegrationFlow.from(Lesson03Channels.BROADCAST_EVENTS)
                // This is the second subscriber to the same PublishSubscribeChannel.
                // One broadcast message is delivered to both subscribers.
                .handle(recorder, "recordNotification")
                .get();
    }
}
