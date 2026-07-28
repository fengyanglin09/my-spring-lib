package io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model;

import java.util.List;

/**
 * Result returned when the lesson tries to put work into the bounded queue.
 */
public record Lesson10EnqueueResult(
        String workId,
        boolean accepted,
        int queueSize,
        int remainingCapacity,
        List<String> lessonTrail
) {
}
