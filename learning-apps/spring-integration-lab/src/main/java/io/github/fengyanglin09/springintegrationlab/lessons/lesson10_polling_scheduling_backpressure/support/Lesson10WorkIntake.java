package io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.support;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model.Lesson10EnqueueResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model.Lesson10QueueSnapshot;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model.Lesson10WorkItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Small helper that submits work to the bounded QueueChannel.
 */
// @Component tells Spring:
// "Create one Lesson10WorkIntake object during startup."
//
// This class is not an IntegrationFlow. It is ordinary Java code that acts like
// a producer. Its job is to try to put messages into the queue so the lesson can
// show what happens when the queue has room and when the queue is full.
@Component
public class Lesson10WorkIntake {

    private final QueueChannel workQueue;

    public Lesson10WorkIntake(@Qualifier(Lesson10Channels.WORK_QUEUE) QueueChannel workQueue) {
        this.workQueue = workQueue;
    }

    public Lesson10EnqueueResult submit(Lesson10WorkItem workItem) {
        // Build a Spring message from the work item.
        //
        // Payload:
        // The actual business data: Lesson10WorkItem.
        //
        // Header:
        // lesson10SubmittedBy is metadata. The poller-side handler can read it
        // later without changing the payload object.
        Message<Lesson10WorkItem> message = MessageBuilder
                .withPayload(workItem)
                .setHeader(Lesson10Headers.SUBMITTED_BY, "lesson10-work-intake")
                .build();

        // send(message, 0) means:
        // "Try to put this message in the QueueChannel, but do not wait if the
        // queue is already full."
        //
        // The timeout value is 0 milliseconds.
        //
        // - If the queue has room, send(...) returns true.
        // - If the queue is full, send(...) returns false immediately.
        //
        // This is the backpressure signal in this lesson. Instead of accepting
        // unlimited work, the queue tells the producer "not right now."
        boolean accepted = workQueue.send(message, 0);

        return new Lesson10EnqueueResult(
                workItem.workId(),
                accepted,
                workQueue.getQueueSize(),
                workQueue.getRemainingCapacity(),
                List.of(accepted
                        ? "backpressure:accepted-into-bounded-queue"
                        : "backpressure:rejected-because-queue-is-full")
        );
    }

    public Lesson10QueueSnapshot snapshot() {
        // These two numbers are useful when learning:
        //
        // queueSize tells us how many messages are currently waiting.
        // remainingCapacity tells us how many more messages can be accepted.
        return new Lesson10QueueSnapshot(
                workQueue.getQueueSize(),
                workQueue.getRemainingCapacity()
        );
    }

    public void clearQueue() {
        // Tests stop the poller and clear the queue before each example so one
        // test's messages do not affect the next test.
        workQueue.clear();
    }
}
