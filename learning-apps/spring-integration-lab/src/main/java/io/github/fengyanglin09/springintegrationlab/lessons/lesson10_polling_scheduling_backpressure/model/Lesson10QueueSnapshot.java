package io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model;

/**
 * Small read model for the queue's current pressure.
 */
public record Lesson10QueueSnapshot(
        int queueSize,
        int remainingCapacity
) {
}
