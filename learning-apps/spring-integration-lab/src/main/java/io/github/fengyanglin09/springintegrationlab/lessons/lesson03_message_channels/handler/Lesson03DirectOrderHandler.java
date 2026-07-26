package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model.Lesson03DeliveryRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model.Lesson03DirectDeliveryReport;
import org.springframework.stereotype.Component;

/**
 * Handles messages sent to the DirectChannel.
 */
// @Component makes this handler available for the direct-channel flow.
@Component
public class Lesson03DirectOrderHandler {

    public Lesson03DirectDeliveryReport handle(Lesson03DeliveryRequest request) {
        // DirectChannel does not store the message for later.
        // It immediately invokes one subscribed handler in the sender's thread.
        return new Lesson03DirectDeliveryReport(
                request.messageId(),
                "direct-order-handler",
                Thread.currentThread().getName()
        );
    }
}
