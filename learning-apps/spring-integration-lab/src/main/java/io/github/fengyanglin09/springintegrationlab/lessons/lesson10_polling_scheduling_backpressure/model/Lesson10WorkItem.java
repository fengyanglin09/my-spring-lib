package io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model;

/**
 * Payload submitted to the bounded work queue.
 */
public record Lesson10WorkItem(
        String workId,
        String description
) {
}
