package io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model.Lesson10ProcessedWork;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model.Lesson10WorkItem;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.support.Lesson10Headers;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Records work handled by the lesson 10 polling consumer.
 */
// @Component tells Spring:
// "Create one Lesson10WorkRecorder object during startup."
@Component
public class Lesson10WorkRecorder {

    // CopyOnWriteArrayList is a thread-safe list.
    //
    // Why use it here?
    //
    // The polling consumer records processed work on a Spring scheduler thread,
    // while the Groovy test reads the list from the test thread. This collection
    // lets one thread add items while another thread reads a stable snapshot.
    private final List<Lesson10ProcessedWork> processedWork = new CopyOnWriteArrayList<>();

    public void record(Lesson10WorkItem workItem, MessageHeaders headers) {
        // This method is called by the polling flow when a message is pulled
        // from the QueueChannel.
        //
        // The payload is the Lesson10WorkItem.
        // The headers are the same headers that were attached during submit(...).
        Object submittedBy = headers.get(Lesson10Headers.SUBMITTED_BY);

        processedWork.add(new Lesson10ProcessedWork(
                workItem.workId(),
                workItem.description(),
                submittedBy == null ? "unknown" : submittedBy.toString(),
                Thread.currentThread().getName(),
                List.of(
                        "poller:received-message-from-queue",
                        "handler:record-processed-work"
                )
        ));
    }

    public List<Lesson10ProcessedWork> processedWork() {
        return List.copyOf(processedWork);
    }

    public void clear() {
        processedWork.clear();
    }
}
