package io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.handler.Lesson10WorkRecorder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model.Lesson10WorkItem;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.support.Lesson10Channels;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.support.Lesson10EndpointIds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;

import java.time.Duration;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson10PollingBackpressureFlow {

    // @Bean tells Spring:
    // "Create this IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson10PollingBackpressureIntegrationFlow(Lesson10WorkRecorder recorder) {
        // This flow reads as:
        //
        // bounded queue
        // -> poll the queue on a schedule
        // -> handle a limited number of queued messages per poll
        return IntegrationFlow.from(Lesson10Channels.WORK_QUEUE)
                // handle(...) creates the endpoint that receives messages from
                // the QueueChannel.
                //
                // Because Lesson10Channels.WORK_QUEUE is a QueueChannel, this
                // endpoint is a polling consumer. It does not receive messages
                // at the exact moment they are sent. Instead, its poller checks
                // the queue on a schedule and pulls messages out.
                .handle(
                        Lesson10WorkItem.class,
                        (workItem, headers) -> {
                            // workItem is the payload pulled from the queue.
                            // headers are the metadata attached to that message.
                            recorder.record(workItem, headers);

                            // Returning null means:
                            // "This handler does not produce an output message."
                            //
                            // A reply message is the message that a handler
                            // returns to the next step in the flow.
                            //
                            // Example:
                            //
                            // - If this handler returned a Lesson10ProcessedWork
                            //   object, Spring Integration could wrap that
                            //   object in a new Message and send it to another
                            //   endpoint after this handle(...) step.
                            // - Because this handler returns null, there is no
                            //   new Message for another endpoint to receive.
                            //
                            // Important:
                            // "No output message" means this current message's
                            // path stops here. It does not stop the whole
                            // polling endpoint forever. The poller can still
                            // receive the next queued message and call this
                            // handler again.
                            //
                            // If we added another endpoint after this
                            // handle(...) step, that later endpoint would not
                            // run for this message unless this handler returned
                            // a non-null value.
                            //
                            // This is true for each individual message handled
                            // by the poller. If one poll handles 2 messages,
                            // this lambda is called once for the first message
                            // and once for the second message. Returning null
                            // stops the path for each message separately.
                            return null;
                        },
                        endpoint -> endpoint
                                // id(...) gives this polling endpoint a stable
                                // bean name. The test uses this id to start and
                                // stop the poller exactly when it is ready.
                                .id(Lesson10EndpointIds.POLLING_WORKER)
                                // autoStartup(false) means:
                                // "Create this polling endpoint during Spring
                                // startup, but do not start polling yet."
                                //
                                // Why turn off auto startup?
                                //
                                // Normally, a polling endpoint can start
                                // automatically when Spring starts the
                                // application context. For this lesson, we do
                                // not want that because the test needs to fill
                                // the QueueChannel first.
                                //
                                // If the poller started immediately, it could
                                // drain messages while the test is still trying
                                // to prove that the bounded queue can fill up.
                                //
                                // This keeps the lesson deterministic:
                                // the test can fill the queue first, then start
                                // the poller and observe what happens.
                                .autoStartup(false)
                                // poller(...) configures the scheduler for this
                                // polling consumer.
                                //
                                // Fixed delay means:
                                // "Wait this long after one poll finishes before
                                // starting the next poll."
                                //
                                // The first Duration is the delay between polls.
                                // The second Duration is the initial delay before
                                // the first poll. Duration.ZERO means the first
                                // poll can run as soon as the endpoint starts.
                                .poller(Pollers.fixedDelay(Duration.ofSeconds(10), Duration.ZERO)
                                        // maxMessagesPerPoll(2) means:
                                        // "During one poll, pull and handle at
                                        // most 2 messages from the queue."
                                        //
                                        // If 3 messages are waiting, the first
                                        // poll handles 2 and leaves 1 message in
                                        // the queue for a later poll.
                                        //
                                        // This does not mean the handler receives
                                        // a List of 2 work items.
                                        //
                                        // It means one poll cycle is allowed to
                                        // repeat this receive-and-handle process
                                        // up to 2 times:
                                        //
                                        // - receive message 1, call the handler
                                        //   with one workItem
                                        // - receive message 2, call the handler
                                        //   with one workItem
                                        // - stop this poll because the limit of
                                        //   2 messages has been reached
                                        .maxMessagesPerPoll(2)
                                        // receiveTimeout(0) means:
                                        // "If the queue is empty, do not wait
                                        // inside receive(...). Return immediately."
                                        //
                                        // This setting works together with
                                        // maxMessagesPerPoll(2).
                                        //
                                        // The poller may try to receive up to 2
                                        // messages during one poll. If it
                                        // receives fewer than 2 because the
                                        // queue becomes empty, receiveTimeout
                                        // controls how long it waits for the
                                        // next message before giving up on this
                                        // poll.
                                        //
                                        // Example with receiveTimeout(0):
                                        //
                                        // - first receive gets message 1
                                        // - second receive sees the queue is
                                        //   empty
                                        // - second receive returns immediately
                                        // - this poll ends with only 1 message
                                        //   handled
                                        //
                                        // If this were receiveTimeout(500), the
                                        // second receive would wait up to 500 ms
                                        // for another message to arrive before
                                        // ending this poll.
                                        //
                                        // This is about maxMessagesPerPoll, not
                                        // QueueChannel capacity. Queue capacity
                                        // controls how many messages can wait in
                                        // the channel. maxMessagesPerPoll
                                        // controls how many waiting messages one
                                        // poll is allowed to handle.
                                        //
                                        // This keeps an empty queue from tying up
                                        // the scheduler thread.
                                        .receiveTimeout(0)))
                .get();
    }
}
