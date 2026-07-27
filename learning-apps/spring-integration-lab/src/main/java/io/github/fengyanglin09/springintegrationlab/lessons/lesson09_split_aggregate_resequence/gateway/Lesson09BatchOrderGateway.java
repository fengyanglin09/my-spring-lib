package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09BatchOrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09OrderSummary;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.support.Lesson09Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 09 split, aggregate, and resequence flow.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
@MessagingGateway
public interface Lesson09BatchOrderGateway {

    // @Gateway says:
    // "When price(...) is called, send the payload to lesson09BatchOrders."
    //
    // @Payload says:
    // "Use this method argument as the message payload."
    //
    // The caller sends one Lesson09BatchOrderRequest and receives one
    // Lesson09OrderSummary. Inside the flow, that one request temporarily
    // becomes many line-item messages, but the gateway hides that internal
    // fan-out and fan-in from the caller.
    //
    // The returned Lesson09OrderSummary comes from the aggregator's
    // outputProcessor step in Lesson09SplitAggregateResequenceFlow.
    @Gateway(requestChannel = Lesson09Channels.BATCH_ORDERS)
    Lesson09OrderSummary price(@Payload Lesson09BatchOrderRequest request);
}
