package io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model;

import java.util.List;

/**
 * Record captured after the polling consumer handles one queued message.
 */
public record Lesson10ProcessedWork(
        String workId,
        String description,
        String submittedBy,
        String pollerThreadName,
        List<String> lessonTrail
) {
}
