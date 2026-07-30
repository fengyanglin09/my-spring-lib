package io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13OrderDraft;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13OrderReviewResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.support.Lesson13Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 13 order review flow.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
@MessagingGateway
public interface Lesson13OrderReviewGateway {

    // @Gateway says:
    // "When review(...) is called, send a message to lesson13OrderReviewRequests."
    //
    // @Payload says:
    // "Use this method argument as the message payload."
    //
    // The gateway is useful in full flow tests because it exercises the same
    // entry point application code would use: gateway -> channel -> flow ->
    // reply.
    @Gateway(requestChannel = Lesson13Channels.ORDER_REVIEW_REQUESTS)
    Lesson13OrderReviewResult review(@Payload Lesson13OrderDraft draft);
}
