package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.handler.Lesson09BatchOrderProcessor;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09BatchOrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09LineItemWork;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.support.Lesson09Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson09SplitAggregateResequenceFlow {

    // @Bean tells Spring:
    // "Create this IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson09SplitAggregateResequenceIntegrationFlow(
            Lesson09BatchOrderProcessor processor,
            ThreadPoolTaskExecutor lesson09LineItemExecutor
    ) {
        // This flow reads as:
        //
        // one batch order message
        // -> split into many line-item messages
        // -> process line items on executor threads
        // -> price each line item
        // -> resequence line-item messages back into original order
        // -> aggregate line-item messages into one order summary
        return IntegrationFlow.from(Lesson09Channels.BATCH_ORDERS)
                // split(...) creates a splitter endpoint.
                //
                // Splitter means:
                // "Turn one message into multiple related messages."
                //
                // The typed lambda form says:
                //
                // - expect the payload to be Lesson09BatchOrderRequest
                // - call processor.splitBatch(batch)
                // - use each item in the returned List as the payload of a new
                //   message
                //
                // Very important:
                // processor.splitBatch(batch) returns a List, but the next step
                // does not receive one message whose payload is that whole List.
                //
                // Instead, Spring Integration loops over the List and creates
                // one new message for each List item.
                //
                // If splitBatch(...) returns:
                //
                // - line item 1
                // - line item 2
                // - line item 3
                //
                // then this splitter sends three separate messages forward:
                //
                // - message payload = line item 1
                // - message payload = line item 2
                // - message payload = line item 3
                //
                // Each of those separate messages is sent to the next step in
                // the flow: the executor channel below.
                //
                // Important:
                // Spring Integration adds correlation and sequence headers to
                // the split messages. Those headers are how later endpoints know
                // which line items belong together and what their original order
                // was.
                .split(Lesson09BatchOrderRequest.class, batch -> processor.splitBatch(batch))
                // channel(MessageChannels.executor(...)) creates an executor
                // channel at this point in the flow.
                //
                // A DirectChannel calls the next step immediately on the same
                // thread. An executor channel hands the message to a thread
                // pool. That means line-item messages can be priced in parallel.
                //
                // Parallel work is useful, but it can change completion order:
                // line 3 might finish before line 1. That is why the next
                // ordering concept, resequence(), matters.
                .channel(MessageChannels.executor(lesson09LineItemExecutor))
                // transform(...) changes each split message payload.
                //
                // Before this step: payload is Lesson09LineItemWork.
                // After this step: payload is Lesson09PricedLineItem.
                //
                // What happens to the headers?
                //
                // processor.priceLineItem(lineItem) returns a new payload
                // object, not a full Message object. In that common case,
                // Spring Integration builds a new output Message for us and
                // copies the input message headers to that new Message.
                //
                // So after this transform, the message still has the splitter
                // headers:
                //
                // - CORRELATION_ID
                // - SEQUENCE_NUMBER
                // - SEQUENCE_SIZE
                //
                // That matters because the next step, resequence(), still needs
                // those headers to know which messages belong together and what
                // order they should be released in.
                //
                // Different case:
                // If this transformer returned a full Message<?> instead of
                // only returning Lesson09PricedLineItem, then our code would be
                // responsible for building that Message correctly, including
                // any headers we still need downstream.
                //
                // This is still one message per line item. We have not combined
                // anything back together yet.
                .transform(Lesson09LineItemWork.class, lineItem -> processor.priceLineItem(lineItem))
                // resequence() creates a resequencer endpoint.
                //
                // Resequencer means:
                // "Use sequence headers to release related messages in their
                // original order."
                //
                // How does it know the original order?
                //
                // The splitter above created one message for each item in the
                // List returned by processor.splitBatch(batch). While doing
                // that, Spring Integration added headers like this:
                //
                // - CORRELATION_ID: same value for all messages from this batch
                // - SEQUENCE_NUMBER: 1 for the first List item, 2 for the
                //   second List item, 3 for the third List item, and so on
                // - SEQUENCE_SIZE: total number of List items
                //
                // The executor channel can make messages arrive here out of
                // order. For example, the message with SEQUENCE_NUMBER = 3 may
                // arrive before the message with SEQUENCE_NUMBER = 1.
                //
                // The resequencer uses CORRELATION_ID to collect messages from
                // the same original batch. Then it uses SEQUENCE_NUMBER to
                // release them downstream in order:
                //
                // 1, then 2, then 3
                //
                // Important:
                // "Original order" means the order of the List returned by the
                // splitter method. The resequencer is not reading our
                // lineNumber field. It is reading Spring Integration's sequence
                // headers.
                //
                // It does not combine messages. After this step, there is still
                // one message per line item. The difference is that downstream
                // endpoints receive those messages in sequence-number order.
                .resequence()
                // aggregate(...) creates an aggregator endpoint.
                //
                // Aggregator means:
                // "Hold related messages until the complete group is present,
                // then turn that group into one output message."
                //
                // By default, the splitter's CORRELATION_ID and SEQUENCE_SIZE
                // headers give the aggregator enough information to know:
                //
                // - which messages belong to the same batch
                // - how many messages must arrive before the group is complete
                //
                // outputProcessor(...) says:
                // "When the group is complete, call this code to build the
                // final payload."
                //
                // Lesson boundary:
                // This lesson assumes every split message eventually reaches
                // the aggregator, so the group can become complete.
                //
                // In a production flow, you would also decide what happens if
                // one split message fails, is rejected by an executor, or never
                // arrives. That usually means configuring timeouts, discard
                // behavior, partial-result behavior, or error handling.
                //
                // We are leaving those policies for the later error-handling
                // and backpressure lessons so this lesson can stay focused on
                // the happy path:
                //
                // split -> resequence -> aggregate
                //
                // processor.summarize(...) returns Lesson09OrderSummary, so the
                // gateway caller receives Lesson09OrderSummary.
                .aggregate(aggregator -> aggregator
                        .outputProcessor(group -> processor.summarize(group))
                        // Once the summary is produced, this lesson does not
                        // need to keep the completed message group in memory.
                        .expireGroupsUponCompletion(true))
                .get();
    }
}
